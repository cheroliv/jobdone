package toodoo.service

import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import kotlin.test.Test


class ConsoleRunSingleTest {
    @Test
    fun run_main_as_test(): Unit =
        startKoin {
            modules(modules = module {
                single<TodoService> { TodoServiceInMemory() }
            })
        }.run { println(get<TodoService>(TodoService::class.java).greetings()) }
}