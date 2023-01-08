package com.example.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.todoapp.R.id.navHostFragment
import com.example.todoapp.R.layout.activity_main

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        navController = (supportFragmentManager
            .findFragmentById(navHostFragment) as NavHostFragment).navController
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp() =        navController.navigateUp() || super.onSupportNavigateUp()
}