package com.example.swiftcafe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.swiftcafe.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation controller and bottom navigation view
        val navController = findNavController(R.id.fragmentContainerView)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)
    }
}
