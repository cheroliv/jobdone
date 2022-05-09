package toodoo.service

interface TodoService {
    fun greetings():String
    fun allTodos(): List<Any>
    fun saveTodo(todo: List<Any>)
}