package org.fred.reminder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.fred.reminder.persistence.Reminder
import org.fred.reminder.persistence.ReminderRepository
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class ReminderBot() : TelegramLongPollingBot(DefaultBotOptions().apply {
    httpProxy = HttpHost("88.99.213.13", 3128)
    credentialsProvider = BasicCredentialsProvider().apply {
        setCredentials(AuthScope("88.99.213.13", 3128), UsernamePasswordCredentials("mkproxy", "58yGPatitCc7m3u9"))
    }}) {
    @Autowired lateinit var json: ObjectMapper
    @Autowired lateinit var reminderRepository: ReminderRepository

    final val client: WhenClient = WhenClient("when-srv-deployment", 50051)
    final val russianLocale = Locale("ru", "RU")
    final val parseFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    final val outFormatter = DateTimeFormatter.ofPattern("d LLLL yyyy", russianLocale)
    final val weekDayFormatter = DateTimeFormatter.ofPattern("cccc", russianLocale)
    final val monthDayFormatter = DateTimeFormatter.ofPattern("d", russianLocale)

    override fun getBotToken(): String = "521757582:AAFx9NWGSU81cxH2l4eFPXNWtEm0nTfqxsg"
    override fun getBotUsername(): String = "ReminderBot"

    override fun onUpdateReceived(update: Update?) {
        if (update == null) {
            return
        }
        if (update.hasCallbackQuery()) {
            val reminder = json.readValue<ReminderDTO>(update.callbackQuery.data)
            val answerText = when (reminder.repeatMode) {
                RepeatMode.ONCE -> "Ок, я напомню вам ${outFormatter.format(reminder.remindDate)} о ${reminder.remindText}"
                RepeatMode.WEEKY -> "Буду напоминать каждый ${weekDayFormatter.format(reminder.remindDate)} о ${reminder.remindText}"
                RepeatMode.MONTHLY -> "${monthDayFormatter.format(reminder.remindDate)} числа каждого месяца напомню о ${reminder.remindText}"
            }
            val answer = AnswerCallbackQuery()
                    .setCallbackQueryId(update.callbackQuery.id)
                    .setText(answerText)
            reminderRepository.save(
                    Reminder(
                            id = UUID.randomUUID().toString(),
                            remindText = reminder.remindText,
                            remindDate = reminder.remindDate,
                            repeatMode = reminder.repeatMode
                    )
            )
            try {
                execute<Boolean, AnswerCallbackQuery>(answer)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            val sourceText = update.message.text
            val parsed = client.parse(update.getMessage().text)

            val message = if (parsed.startsWith("ERROR")) {
                SendMessage()
                        .setChatId(update.getMessage().chatId)
                        .setText("Простите, я не понял что вы хотели этим сказать")
            } else {
                val parts = parsed.split('/')
                val dt = parseFormatter.parse(parts[0])
                val dateMetionIndex = sourceText.indexOf(parts[1])
                val reminder = ReminderDTO(
                        remindText = sourceText.removeRange(dateMetionIndex, dateMetionIndex+parts[1].length),
                        remindDate = LocalDateTime.from(dt),
                        repeatMode = RepeatMode.ONCE
                )
                val inlineKb = InlineKeyboardMarkup()
                val keyboardRow1 = mutableListOf(InlineKeyboardButton("Напомнить один раз ${outFormatter.format(dt)}").setCallbackData(json.writeValueAsString(reminder)))
                val keyboardRow2 = mutableListOf(InlineKeyboardButton("Напомнить каждый ${weekDayFormatter.format(dt)}").setCallbackData(json.writeValueAsString(reminder.copy(repeatMode = RepeatMode.WEEKY)))                    )
                val keyboardRow3 = mutableListOf(InlineKeyboardButton("Напомнить ${monthDayFormatter.format(dt)} числа каждого месяца").setCallbackData(json.writeValueAsString(reminder.copy(repeatMode = RepeatMode.MONTHLY)))
)
                inlineKb.setKeyboard(mutableListOf(keyboardRow1, keyboardRow2, keyboardRow3))

                SendMessage()
                        .setChatId(update.getMessage().chatId)
                        .setText(parsed)
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

data class ReminderDTO (
        val remindText:String,
        val remindDate: LocalDateTime,
        val repeatMode: RepeatMode
)

enum class RepeatMode {
    ONCE, WEEKY, MONTHLY
}