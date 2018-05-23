package org.fred.reminder.persistence

import org.fred.reminder.RepeatMode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime

interface ReminderRepository:MongoRepository<Reminder, String>

data class Reminder (
        @Id
        val id:String,
        val chatId:Long,
        val remindText:String,
        val remindDate:LocalDateTime,
        val repeatMode: RepeatMode? = null
)



