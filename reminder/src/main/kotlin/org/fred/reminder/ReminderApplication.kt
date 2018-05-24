package org.fred.reminder

import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.quartz.SpringBeanJobFactory
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

class AutoWiringSpringBeanJobFactory: SpringBeanJobFactory(), ApplicationContextAware {
    private lateinit var beanFactory: AutowireCapableBeanFactory

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        beanFactory = applicationContext.autowireCapableBeanFactory
    }

    override fun createJobInstance(bundle: TriggerFiredBundle): Any {
        val job = super.createJobInstance(bundle)
        beanFactory.autowireBean(job)
        return job
    }
}


@Configuration
class SchedulerConfig {
    @Bean
    fun autowiringJobFactory():JobFactory {
        return AutoWiringSpringBeanJobFactory()
    }


    @Bean
    fun shedulerCustomizer(jobFactory: JobFactory): SchedulerFactoryBeanCustomizer {
        return SchedulerFactoryBeanCustomizer() {schedulerFactoryBean ->
            LoggerFactory.getLogger(SchedulerConfig::class.java).info("Schedule factory customized")
            schedulerFactoryBean.setJobFactory(jobFactory)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ReminderApplication>(*args)
}
