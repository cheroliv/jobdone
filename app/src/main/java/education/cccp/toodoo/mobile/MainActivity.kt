package education.cccp.toodoo.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import education.cccp.toodoo.mobile.R.id.container
import education.cccp.toodoo.mobile.R.layout.main_activity
import education.cccp.toodoo.mobile.ui.main.MainFragment


class MainActivity : AppCompatActivity() {
    //        education.cccp.data.datas

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