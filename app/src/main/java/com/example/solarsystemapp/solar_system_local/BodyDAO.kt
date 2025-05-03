package com.example.solarsystemapp.solar_system_local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BodyDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBody(body: DataBodyLocal)

    @Query("SELECT * FROM body")
    suspend fun getAllBodies(): List<DataBodyLocal>


    @Query("SELECT * FROM body WHERE bodyType = :bodyType")
    suspend fun getBodiesByType(bodyType: String): List<DataBodyLocal>


    @Query("SELECT * FROM body WHERE id=:id")
    suspend fun getBodyById(id: String): DataBodyLocal?

    @Delete
    suspend fun deleteBody(body: DataBodyLocal)


}