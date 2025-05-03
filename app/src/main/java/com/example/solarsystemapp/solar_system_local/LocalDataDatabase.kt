package com.example.solarsystemapp.solar_system_local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DataBodyLocal::class, DataApodLocal::class], version = 1)
abstract class LocalDataDatabase : RoomDatabase(){
    abstract fun getBodyDao(): BodyDAO
    abstract fun getApodDao(): ApodDAO
}