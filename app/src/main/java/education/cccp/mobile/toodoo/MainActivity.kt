package education.cccp.mobile.toodoo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import education.cccp.mobile.toodoo.R.id.container
import education.cccp.mobile.toodoo.R.layout.main_activity
import education.cccp.mobile.toodoo.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(container, MainFragment.newInstance())
                .commitNow()
        }
    }
}