package education.cccp.toodoo.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import education.cccp.toodoo.mobile.R.id.container
import education.cccp.toodoo.mobile.R.layout.main_activity
import education.cccp.toodoo.mobile.ui.main.MainFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(main_activity)
//        datas
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(container, MainFragment.newInstance())
                .commitNow()
        }
    }
}