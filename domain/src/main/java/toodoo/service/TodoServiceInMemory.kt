package toodoo.service

import toodoo.service.TodoServiceInMemory.InMemoryData.addTodo

class TodoServiceInMemory : TodoService {
    object InMemoryData {
        @JvmStatic
        private val repo: MutableList<Any> = mutableListOf()

        @JvmStatic
        fun allTodos() = repo

        @JvmStatic
        fun addTodo(todos: List<Any>) {
            repo.add(todos)
        }
    }

    override fun greetings() = "Hello from ${this::class.java.simpleName}!"
    override fun allTodos(): List<Any> = allTodos()
    override fun saveTodo(todo: List<Any>) = addTodo(todo)
}