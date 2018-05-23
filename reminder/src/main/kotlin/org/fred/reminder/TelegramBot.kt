package org.fred.reminder

import com.fasterxml.jackson.databind.ObjectMapper
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
class ReminderBot : TelegramLongPollingBot(DefaultBotOptions().apply {
    httpProxy = HttpHost("88.99.213.13", 3128)
    credentialsProvider = BasicCredentialsProvider().apply {
        setCredentials(AuthScope("88.99.213.13", 3128), UsernamePasswordCredentials("mkproxy", "58yGPatitCc7m3u9"))
    }}) {
    @Autowired lateinit var json: ObjectMapper
    @Autowired lateinit var reminderRepository: ReminderRepository

    private final val client: WhenClient = WhenClient("when-srv-deployment", 50051)
    private final val russianLocale = Locale("ru", "RU")
    private final val parseFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    private final val outFormatter = DateTimeFormatter.ofPattern("d LLLL yyyy", russianLocale)
    private final val weekDayFormatter = DateTimeFormatter.ofPattern("cccc", russianLocale)
    private final val monthDayFormatter = DateTimeFormatter.ofPattern("d", russianLocale)

    override fun getBotToken(): String = "521757582:AAFx9NWGSU81cxH2l4eFPXNWtEm0nTfqxsg"
    override fun getBotUsername(): String = "ReminderBot"

    override fun onUpdateReceived(update: Update?) {
        if (update == null) {
            return
        }
        if (update.hasCallbackQuery()) {
            val callbackParts = update.callbackQuery.data.split("|")
            val callback = AnswerCallback(callbackParts[0], RepeatMode.valueOf(callbackParts[1]))
            val reminder = reminderRepository.findById(callback.id).orElseGet(null) ?: return
            val answerText = when (callback.mode) {
                RepeatMode.ONCE -> "Ок, я напомню вам ${outFormatter.format(reminder.remindDate)} о ${reminder.remindText}"
                RepeatMode.WEEKY -> "Буду напоминать каждый ${weekDayFormatter.format(reminder.remindDate)} о ${reminder.remindText}"
                RepeatMode.MONTHLY -> "${monthDayFormatter.format(reminder.remindDate)} числа каждого месяца напомню о ${reminder.remindText}"
            }
            val answer = AnswerCallbackQuery()
                    .setCallbackQueryId(update.callbackQuery.id)
                    .setText(answerText)
            reminderRepository.save(reminder.copy(repeatMode = callback.mode))
            try {
                execute<Boolean, AnswerCallbackQuery>(answer)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }

        }

        if (update.hasMessage() && update.message.hasText()) {
            val sourceText = update.message.text
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
                reminderRepository.save(
                        Reminder(
                                id = reminderId,
                                chatId = update.message.chatId,
                                remindText = sourceText.removeRange(dateMetionIndex, dateMetionIndex+parts[1].length),
                                remindDate = LocalDateTime.from(dt)
                        )
                )


                val inlineKb = InlineKeyboardMarkup()
                val keyboardRow1 = mutableListOf(InlineKeyboardButton("Напомнить один раз ${outFormatter.format(dt)}").setCallbackData("${reminderId}|${RepeatMode.ONCE}"))
                val keyboardRow2 = mutableListOf(InlineKeyboardButton("Напомнить каждый ${weekDayFormatter.format(dt)}").setCallbackData("${reminderId}|${RepeatMode.WEEKY}")                    )
                val keyboardRow3 = mutableListOf(InlineKeyboardButton("Напомнить ${monthDayFormatter.format(dt)} числа каждого месяца").setCallbackData("${reminderId}|${RepeatMode.MONTHLY}"))

                inlineKb.keyboard = mutableListOf(keyboardRow1, keyboardRow2, keyboardRow3)

                SendMessage()
                        .setChatId(update.message.chatId)
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

data class AnswerCallback(
        val id:String,
        val mode:RepeatMode
)

enum class RepeatMode {
    ONCE, WEEKY, MONTHLY
}