package org.fred.reminder

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.springframework.stereotype.Service
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException


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

            val message = SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().chatId)
                    .setText(parsed)
            try {
                execute<Message, SendMessage>(message) // Call method to send the message
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }
}