package todo.service

import todo.model.Organization
import todo.model.Todo

interface TodoService {
    fun allTodos(): List<Todo>
    fun saveTodo(todo: Todo)
    fun allOrganizations(): List<Organization>
    fun saveOrganization(org: Organization)
}