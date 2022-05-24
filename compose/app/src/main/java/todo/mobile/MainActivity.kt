package todo.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import todo.mobile.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() = TodoTheme {
    Greeting("buddy")
    AddTodoFloatingActionButton()
}

@Composable
fun Greeting(name: String) = Text(text = "Hi, $name!")

@Composable
fun AddTodoFloatingActionButton() {

}
