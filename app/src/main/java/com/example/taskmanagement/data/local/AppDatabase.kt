package com.example.taskmanagement.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskmanagement.data.local.dao.AchievementDao
import com.example.taskmanagement.data.local.dao.FocusSessionDao
import com.example.taskmanagement.data.local.dao.GameProfileDao
import com.example.taskmanagement.data.local.dao.LootInventoryDao
import com.example.taskmanagement.data.local.dao.ProfileBackgroundDao
import com.example.taskmanagement.data.local.dao.ProfileSoundDao
import com.example.taskmanagement.data.local.dao.ProfileTomeDao
import com.example.taskmanagement.data.local.dao.QuestClaimDao
import com.example.taskmanagement.data.local.dao.TaskDao
import com.example.taskmanagement.data.local.models.Achievement
import com.example.taskmanagement.data.local.models.FocusSession
import com.example.taskmanagement.data.local.models.GameProfile
import com.example.taskmanagement.data.local.models.LootInventoryItem
import com.example.taskmanagement.data.local.models.ProfileBackground
import com.example.taskmanagement.data.local.models.ProfileSound
import com.example.taskmanagement.data.local.models.ProfileTome
import com.example.taskmanagement.data.local.models.QuestClaim
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.local.type_converters.DateTypeConverter

@Database(
    entities = [
        Task::class,
        FocusSession::class,
        GameProfile::class,
        Achievement::class,
        LootInventoryItem::class,
        ProfileTome::class,
        ProfileSound::class,
        ProfileBackground::class,
        QuestClaim::class
    ],
    version = 21,
    exportSchema = false
)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun gameProfileDao(): GameProfileDao
    abstract fun achievementDao(): AchievementDao
    abstract fun lootInventoryDao(): LootInventoryDao
    abstract fun profileTomeDao(): ProfileTomeDao
    abstract fun profileSoundDao(): ProfileSoundDao
    abstract fun profileBackgroundDao(): ProfileBackgroundDao
    abstract fun questClaimDao(): QuestClaimDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDatabase::class.java,
                    name = "task_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
