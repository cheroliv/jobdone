package com.cheroliv.jobdone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.cheroliv.jobdone.R.id.navHostFragment
import com.cheroliv.jobdone.R.layout.activity_main

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        navController = (supportFragmentManager
            .findFragmentById(navHostFragment) as NavHostFragment).navController
        setupActionBarWithNavController(navController)
    }

}