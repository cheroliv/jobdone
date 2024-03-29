package todo.service

import todo.model.Organization
import todo.model.Todo
import todo.service.TodoServiceInMemory.InMemoryData.addOrganization
import todo.service.TodoServiceInMemory.InMemoryData.addTodo
import todo.service.TodoServiceInMemory.InMemoryData.getAllOrganizations
import todo.service.TodoServiceInMemory.InMemoryData.getAllTodos


open class TodoServiceInMemory : TodoService {
    object InMemoryData {
        @JvmStatic
        private val todos: MutableList<Todo> = mutableListOf()

        @JvmStatic
        fun getAllTodos() = todos

        @JvmStatic
        fun addTodo(todo: Todo) {
            todos.add(todo)
        }

        @JvmStatic
        private val organizations: MutableList<Organization> = mutableListOf()

        @JvmStatic
        fun getAllOrganizations() = organizations

        @JvmStatic
        fun addOrganization(organization: Organization) {
            organizations.add(organization)
        }
    }

    override fun allTodos(): List<Todo> = getAllTodos()
    override fun saveTodo(todo: Todo) = addTodo(todo)
    override fun allOrganizations(): List<Organization> = getAllOrganizations()
    override fun saveOrganization(org: Organization) = addOrganization(org)
}