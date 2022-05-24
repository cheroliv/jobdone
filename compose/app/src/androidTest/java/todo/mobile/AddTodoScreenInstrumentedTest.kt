package todo.mobile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import todo.mobile.ui.theme.TodoTheme


@RunWith(AndroidJUnit4::class)
class AddTodoScreenInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    //TODO: faire le test UI
    @Test
    fun écran_possède_un_bouton_add_todo() {
        // Start the app
        composeTestRule.setContent {
            TodoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("buddy")
                }
            }
        }
//        composeTestRule.onNodeWithText(
//            text = "buddy",
//            ignoreCase = true
//        ).assertIsDisplayed()
    }
}

