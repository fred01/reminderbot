package org.fred.reminder

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException





@SpringBootApplication
class ReminderApplication: CommandLineRunner {
    @Autowired lateinit var reminderBot: ReminderBot

    override fun run(vararg args: String?) {
        val client = WhenClient("localhost", 50051)
        try {
            /* Access a service running on the local machine on port 50051 */
            val respinse = client.greet("world")

        } finally {
            client.shutdown()
        }


        ApiContextInitializer.init()
        val botsApi = TelegramBotsApi()
        try {
            botsApi.registerBot(reminderBot)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ReminderApplication>(*args)
}
