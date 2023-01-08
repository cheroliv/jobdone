package com.example.todoapp.data

import androidx.room.TypeConverter
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.Priority.valueOf

class Converter {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = valueOf(priority)

}