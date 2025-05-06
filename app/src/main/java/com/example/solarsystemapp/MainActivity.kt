package com.example.solarsystemapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.discovernasa.R
import com.example.discovernasa.databinding.ActivityMainBinding
import com.example.solarsystemapp.localdata.LocalDatastore
import com.example.solarsystemapp.nasa_api.ApodNetwork
import com.example.solarsystemapp.wikipedia_api.WikipediaNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
      //  installSplashScreen()
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

    override fun onResume() {
        super.onResume()
        //setupConfiguration()
    }

    private fun setupConfiguration() {
        // Dark mode:
        CoroutineScope(Dispatchers.IO).launch {
            val darkMode = LocalDatastore.readBoolean(this@MainActivity, Tools.DARK_MODE_KEY)
            runOnUiThread {  toggleNightMode(darkMode) }
        }
    }

    private fun setupNavigationController() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        mBinding.bottomNavigationView.setupWithNavController(navController)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                findNavController(R.id.fragmentContainerView).navigate(R.id.action_any_to_settingsFragments)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun toggleNightMode(isEnabled: Boolean) {
        val mode = if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(mode)
        delegate.applyDayNight()
    }
}