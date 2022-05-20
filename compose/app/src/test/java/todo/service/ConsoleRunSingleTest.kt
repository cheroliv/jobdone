package todo.service

import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import todo.service.ConsoleRunSingleTest.TodoServiceGreetings.TodoServiceGreetingsInMemory
import kotlin.test.Test
import kotlin.test.assertEquals


class ConsoleRunSingleTest {

    interface TodoServiceGreetings : TodoService {
        fun greetings(): String
        class TodoServiceGreetingsInMemory : TodoServiceGreetings, TodoServiceInMemory() {
            override fun greetings() = "Hello from ${this::class.java.simpleName}!"
        }
    }

    @Test
    fun run_main_as_test(): Unit =
        startKoin {
            modules(modules = module {
                single<TodoServiceGreetings> { TodoServiceGreetingsInMemory() }
            })
        }.run {
            get<TodoServiceGreetings>(TodoServiceGreetings::class.java).greetings().apply {
                assertEquals(
                    expected = "Hello from ${TodoServiceGreetingsInMemory::class.java.simpleName}!",
                    actual = this
                )
                println(this)
            }
        }
}