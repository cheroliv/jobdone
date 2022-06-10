package jobdone.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jobdone.mobile.ui.main.MainFragment


class MainActivity : AppCompatActivity() {
    //        education.cccp.data.datas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}