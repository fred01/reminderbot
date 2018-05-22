package org.fred.reminder

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.springframework.stereotype.Service
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Service
class ReminderBot() : TelegramLongPollingBot(DefaultBotOptions().apply {
    httpProxy = HttpHost("88.99.213.13", 3128)
    credentialsProvider = BasicCredentialsProvider().apply {
        setCredentials(AuthScope("88.99.213.13", 3128), UsernamePasswordCredentials("mkproxy", "58yGPatitCc7m3u9"))
    }
}) {
    final val client: WhenClient

    init {
        this.client = WhenClient("when-srv-deployment", 50051)
    }

    override fun getBotToken(): String = "521757582:AAFx9NWGSU81cxH2l4eFPXNWtEm0nTfqxsg"

    override fun getBotUsername(): String = "ReminderBot"

    override fun onUpdateReceived(update: Update?) {
        if (update == null) {
            return
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            val parsed = client.parse(update.getMessage().text)

            val message = if (parsed.startsWith("ERROR")) {
                SendMessage()
                        .setChatId(update.getMessage().chatId)
                        .setText("Простите, я не понял что вы хотели этим сказать")
            } else {
                val parts = parsed.split('/')
                val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
                val outFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                val outFormatter1 = DateTimeFormatter.ofPattern("EEE")
                val outFormatter2 = DateTimeFormatter.ofPattern("d")
                val dt = formatter.parse(parts[0])

                val inlineKb = InlineKeyboardMarkup()
                val keyboardRow1 = mutableListOf(InlineKeyboardButton("Напомнить один раз ${outFormatter.format(dt)}").apply {
                    callbackData = "remind once"
                })
                val keyboardRow2 = mutableListOf(InlineKeyboardButton("Напомнить каждый ${outFormatter1.format(dt)}").apply {
                    callbackData = "remind weekly"
                })
                val keyboardRow3 = mutableListOf(InlineKeyboardButton("Напомнить ${outFormatter2.format(dt)} числа каждого месяца").apply {
                    callbackData = "remind monthly"
                })
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