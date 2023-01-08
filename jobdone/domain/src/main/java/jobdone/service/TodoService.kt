package jobdone.service

import jobdone.model.Organization
import jobdone.model.Todo

interface TodoService {
    fun allTodos(): List<Todo>
    fun saveTodo(todo: Todo)
    fun allOrganizations(): List<Organization>
    fun saveOrganization(org: Organization)
}