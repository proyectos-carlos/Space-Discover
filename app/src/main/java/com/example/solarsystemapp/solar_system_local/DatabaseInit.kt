package com.example.solarsystemapp.solar_system_local

import android.app.Application
import androidx.room.Room

class DatabaseInit : Application() {
    companion object {
        lateinit var localDataDatabase: LocalDataDatabase
        const val DATABASE_NAME = "LOCAL_SQLITE_DATABASE"


    }


    override fun onCreate() {
        super.onCreate()
        setupLocalDatabase()
    }

    private fun setupLocalDatabase() {
        localDataDatabase = Room.databaseBuilder(
            this,
            LocalDataDatabase::class.java,
            DATABASE_NAME).build()
    }
}