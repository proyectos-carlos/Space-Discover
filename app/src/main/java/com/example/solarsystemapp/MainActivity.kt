package com.example.solarsystemapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.discovernasa.R
import com.example.discovernasa.databinding.ActivityMainBinding
import com.example.solarsystemapp.nasa_api.ApodNetwork
import com.example.solarsystemapp.wikipedia_api.WikipediaNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityMainBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupNavigationController()

        //Test Wikipedia API
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("BigoActivity", "${WikipediaNetwork.searchWikipediaArticle("Jupiter")}")
        }

        //Test APOD API:
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("BigoActivity", "${ApodNetwork.getApod(date = "2024-04-20")}")
        }
    }

    private fun setupNavigationController() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        mBinding.bottomNavigationView.setupWithNavController(navController)
    }
}