package toodoo.service

import toodoo.model.Organization
import toodoo.model.Todo

interface TodoService {
    fun allTodos(): List<Todo>
    fun saveTodo(todo: Todo)
    fun allOrganizations(): List<Organization>
    fun saveOrganization(org: Organization)
}