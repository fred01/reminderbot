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
        ApiContextInitializer.init()
        val botsApi = TelegramBotsApi()
        try {
            botsApi.registerBot(reminderBot)
        } catch (e: TelegramApiException) {
            println(e.message)
            e.printStackTrace()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ReminderApplication>(*args)
}
