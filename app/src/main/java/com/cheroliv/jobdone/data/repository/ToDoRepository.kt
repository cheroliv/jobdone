package com.cheroliv.jobdone.data.repository

import com.cheroliv.jobdone.data.ToDoDao
import com.cheroliv.jobdone.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllData = toDoDao.getAllData()
    val sortByHighPriority = toDoDao.sortByHighPriority()
    val sortByLowPriority = toDoDao.sortByLowPriority()

    suspend fun insertData(toDoData: ToDoData) = toDoDao.insertData(toDoData)

    suspend fun updateData(toDoData: ToDoData) = toDoDao.updateData(toDoData)

    suspend fun deleteItem(toDoData: ToDoData) = toDoDao.deleteItem(toDoData)

    suspend fun deleteAll() = toDoDao.deleteAll()

    fun searchDatabase(searchQuery: String) = toDoDao.searchDatabase(searchQuery)
}