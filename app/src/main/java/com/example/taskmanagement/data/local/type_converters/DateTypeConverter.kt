package com.example.taskmanagement.data.local.type_converters

import androidx.room.TypeConverter
import java.time.LocalDate

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimeStamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}