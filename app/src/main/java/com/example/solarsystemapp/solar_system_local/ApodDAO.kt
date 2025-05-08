package com.example.solarsystemapp.solar_system_local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ApodDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertApod(apod: DataApodLocal)

    @Query("SELECT * FROM apod")
    suspend fun getAllApod(): List<DataApodLocal>

    @Query("SELECT * FROM apod WHERE date = :date")
    suspend fun getApodByDate(date : String) : DataApodLocal?


    @Delete
    suspend fun deleteApod(apod: DataApodLocal)
}