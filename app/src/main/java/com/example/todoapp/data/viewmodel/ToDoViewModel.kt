package com.example.todoapp.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.ToDoDatabase.Companion.getDatabase
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ToDoViewModel(application: Application) : AndroidViewModel(application) {

    private val toDoDao = getDatabase(application).toDoDao()
    private val repository: ToDoRepository = ToDoRepository(toDoDao)

    val getAllData: LiveData<List<ToDoData>> = repository.getAllData
    val sortByHighPriority: LiveData<List<ToDoData>> = repository.sortByHighPriority
    val sortByLowPriority: LiveData<List<ToDoData>> = repository.sortByLowPriority

    fun insertData(toDoData: ToDoData) {
        viewModelScope.launch(IO) {
            repository.insertData(toDoData)
        }
    }

    fun updateData(toDoData: ToDoData) {
        viewModelScope.launch(IO) { repository.updateData(toDoData) }
    }

    fun deleteItem(toDoData: ToDoData) {
        viewModelScope.launch(IO) { repository.deleteItem(toDoData) }
    }

    fun deleteAll() {
        viewModelScope.launch(IO) { repository.deleteAll() }
    }

    fun searchDatabase(searchQuery: String) = repository.searchDatabase(searchQuery)
}