package org.fred.reminder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException

@SpringBootApplication
@EnableMongoRepositories(basePackages = ["org.fred.reminder.persistence"])
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

@Configuration
class JacksonConfig {

    @Bean
    fun kotlinModule(): SimpleModule = KotlinModule()

    @Bean
    fun jackson():ObjectMapper {
        return jacksonObjectMapper()
    }
}

fun main(args: Array<String>) {
    runApplication<ReminderApplication>(*args)
}
