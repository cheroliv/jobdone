package com.cheroliv.jobdone.data

import androidx.room.TypeConverter
import com.cheroliv.jobdone.data.models.Priority
import com.cheroliv.jobdone.data.models.Priority.valueOf

class Converter {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = valueOf(priority)

}