package org.fred.reminder.persistence

import org.fred.reminder.RepeatMode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime

interface ReminderRepository:MongoRepository<Reminder, String>
interface SettingsRepository:MongoRepository<Settings, Long>

data class Reminder (
        @Id
        val id:String,
        val chatId:Long,
        val remindText:String,
        val remindDate:LocalDateTime,
        val repeatMode: RepeatMode? = null,
        val remindTimestamp:Long,
        val hoursDiff: Int
)

data class Settings (
    @Id
    val chatId:Long,
    val hoursDiff:Int
)


