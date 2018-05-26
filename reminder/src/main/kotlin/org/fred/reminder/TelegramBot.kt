package org.fred.reminder

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.fred.reminder.persistence.Reminder
import org.fred.reminder.persistence.ReminderRepository
import org.fred.reminder.persistence.Settings
import org.fred.reminder.persistence.SettingsRepository
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.JobExecutionContext
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*

class ReminderJob : Job {
    @Suppress("SpringKotlinAutowiredMembers")
    @Autowired
    lateinit var sender: ReminderBot

    private val logger = LoggerFactory.getLogger(ReminderJob::class.java)

    override fun execute(context: JobExecutionContext?) {
        if (context != null) {
            val chatId = context.jobDetail.jobDataMap.get("chatId") as Long
            val message = context.jobDetail.jobDataMap.get("message").toString()

            logger.info("Reminding $message to $chatId")
            val sendMessage = SendMessage()
                    .setChatId(chatId)
                    .setText(message)
            try {
                sender.execute<Message, SendMessage>(sendMessage)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }
}

@Service
class ReminderBot : TelegramLongPollingBot(DefaultBotOptions().apply {
    httpProxy = HttpHost("88.99.213.13", 3128)
    credentialsProvider = BasicCredentialsProvider().apply {
        setCredentials(AuthScope("88.99.213.13", 3128), UsernamePasswordCredentials("mkproxy", "58yGPatitCc7m3u9"))
    }
}) {
    private val logger = LoggerFactory.getLogger(ReminderBot::class.java)

    @Autowired
    lateinit var reminderRepository: ReminderRepository
    @Autowired
    lateinit var settingsRepository: SettingsRepository
    @Autowired
    lateinit var scheduler: Scheduler

    private final val client: WhenClient = WhenClient("when-srv-deployment", 50051)
    private final val russianLocale = Locale("ru", "RU")
    private final val parseFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    private final val outFormatter = DateTimeFormatter.ofPattern("HH:mm d LLLL yyyy ", russianLocale)
    private final val weekDayFormatter = DateTimeFormatter.ofPattern("cccc", russianLocale)
    private final val monthDayFormatter = DateTimeFormatter.ofPattern("d", russianLocale)

    val settingsCache = mutableMapOf<Long, Settings>()

    override fun getBotToken(): String = "521757582:AAFx9NWGSU81cxH2l4eFPXNWtEm0nTfqxsg"
    override fun getBotUsername(): String = "ReminderBot"

    private fun getHoursDiff(chatId: Long): Int? {
        val settings = settingsCache[chatId]
        return if (settings == null) {
            // cache miss, try get from DB
            val found = settingsRepository.findById(chatId)
            if (found.isPresent) {
                found.get().hoursDiff
            } else {
                null
            }
        } else {
            settings.hoursDiff
        }
    }

    override fun onUpdateReceived(update: Update?) {
        if (update == null) {
            return
        }
        if (update.hasCallbackQuery()) {
            val callbackParts = update.callbackQuery.data.split("|")
            val callback = AnswerCallback(callbackParts[0], RepeatMode.valueOf(callbackParts[1]))
            val reminder = reminderRepository.findById(callback.id).orElseGet(null) ?: return
            val answerText = when (callback.mode) {
                RepeatMode.ONCE -> "Ок, я напомню вам ${outFormatter.format(reminder.remindDate.plusHours(reminder.hoursDiff.toLong()))} о ${reminder.remindText}"
                RepeatMode.WEEKY -> "Буду напоминать каждый ${weekDayFormatter.format(reminder.remindDate)} о ${reminder.remindText}"
                RepeatMode.MONTHLY -> "${monthDayFormatter.format(reminder.remindDate)} числа каждого месяца напомню о ${reminder.remindText}"
                RepeatMode.FORGOT -> "Хорошо, забыли :)"
            }
            val answer = AnswerCallbackQuery()
                    .setCallbackQueryId(update.callbackQuery.id)
                    .setText(answerText)
            if (callback.mode != RepeatMode.FORGOT) {
                reminderRepository.save(reminder.copy(repeatMode = callback.mode))
                val scheduleDate = Date((reminder.remindTimestamp + reminder.hoursDiff * 60 * 60)*1000)
                val trigger = TriggerBuilder.newTrigger()
                        .startAt(scheduleDate)
                        .build()

                val jobDetail = newJob().ofType(ReminderJob::class.java)
                        .usingJobData("chatId", reminder.chatId)
                        .usingJobData("message", reminder.remindText)
                        .build()

                logger.info("scheduleJob for ${reminder.chatId} to ${SimpleDateFormat.getDateTimeInstance().format(scheduleDate)}")

                scheduler.scheduleJob(jobDetail, trigger)
            }

            try {
                execute<Boolean, AnswerCallbackQuery>(answer)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }

        if (update.hasMessage() && update.message.hasText()) {
            val sourceText = update.message.text
            val tzPrefix = "мое время: "
            if (sourceText.startsWith(tzPrefix)) {
                val inputHours = sourceText.removePrefix(tzPrefix).trim()

                try {
                    val remoteHours = inputHours.toInt()
                    val myTime = LocalTime.now()

                    val normalizedRemoteHour = if (remoteHours < myTime.hour) {
                        remoteHours + 24
                    } else {
                        remoteHours
                    }

                    val hoursDiff = normalizedRemoteHour - myTime.hour

                    val s = settingsRepository.save(
                            Settings(update.message.chatId, hoursDiff)
                    )

                    settingsCache.put(update.message.chatId, s)
                    val message = SendMessage()
                            .setChatId(update.message.chatId)
                            .setText("А у меня ${myTime.hour}, у нас с вами ${hoursDiff} часов разницы. Ну вот и познакомились :) Теперь можете просить меня напомнить что-нибудь, например 'позвонить через 10 минут' ")
                    try {
                        execute<Message, SendMessage>(message) // Call method to send the message
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    val message = SendMessage()
                            .setChatId(update.message.chatId)
                            .setText("А вот сейчас не понял. Укажите ваше текущее время в формате ЧЧ, например ${tzPrefix} 14")
                    try {
                        execute<Message, SendMessage>(message) // Call method to send the message
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }
                }
            } else {
                val hoursDiff = getHoursDiff(update.message.chatId)
                if (hoursDiff == null) {
                    val message = SendMessage()
                            .setChatId(update.message.chatId)
                            .setText("Простите, я не знаю вашу временную зону, а без нее я буду неправильно вам напоминать. Введите сообщение '${tzPrefix} ЧЧ', а вместо ЧЧ введите сколько у вас сейчас часов. Например '${tzPrefix} 12'. Вводите без кавычек :)")
                    try {
                        execute<Message, SendMessage>(message)
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }
                } else {
                    val parsed = client.parse(update.message.text)

                    val message = if (parsed.startsWith("ERROR")) {
                        SendMessage()
                                .setChatId(update.message.chatId)
                                .setText("Простите, я не понял что вы хотели этим сказать")
                    } else {
                        val parts = parsed.split('/')
                        val dt = parseFormatter.parse(parts[0])
                        val dateMetionIndex = sourceText.indexOf(parts[1])
                        val reminderId = UUID.randomUUID().toString()
                        val r = reminderRepository.save(
                                Reminder(
                                        id = reminderId,
                                        chatId = update.message.chatId,
                                        remindText = sourceText.removeRange(dateMetionIndex, dateMetionIndex + parts[1].length),
                                        remindDate = LocalDateTime.from(dt),
                                        remindTimestamp = dt.getLong(ChronoField.INSTANT_SECONDS),
                                        hoursDiff = hoursDiff
                                )
                        )
                        val inlineKb = InlineKeyboardMarkup()
                        val keyboardRow1 = mutableListOf(InlineKeyboardButton("Напомнить один раз ${outFormatter.format(r.remindDate.plusHours(hoursDiff.toLong()))}").setCallbackData("${reminderId}|${RepeatMode.ONCE}"))
                        val keyboardRow2 = mutableListOf(InlineKeyboardButton("Напомнить каждый ${weekDayFormatter.format(dt)}").setCallbackData("${reminderId}|${RepeatMode.WEEKY}"))
                        val keyboardRow3 = mutableListOf(InlineKeyboardButton("Напомнить ${monthDayFormatter.format(dt)} числа каждого месяца").setCallbackData("${reminderId}|${RepeatMode.MONTHLY}"))
                        val keyboardRow4 = mutableListOf(InlineKeyboardButton("Я передумал, не напоминай мне об этом").setCallbackData("${reminderId}|${RepeatMode.FORGOT}"))

                        inlineKb.keyboard = mutableListOf(keyboardRow1, keyboardRow2, keyboardRow3, keyboardRow4)

                        SendMessage()
                                .setChatId(update.message.chatId)
                                .setText("Вы просите напомнить ${r.remindText} в ${outFormatter.format(r.remindDate.plusHours(r.hoursDiff.toLong()))}, давайте уточним как вам напомнить")
                                .setReplyMarkup(inlineKb)
                    }

                    try {
                        execute<Message, SendMessage>(message) // Call method to send the message
                    } catch (e: TelegramApiException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}

data class AnswerCallback(
        val id: String,
        val mode: RepeatMode
)

enum class RepeatMode {
    ONCE, WEEKY, MONTHLY, FORGOT
}