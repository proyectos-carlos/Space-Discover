package com.example.solarsystemapp.solar_system_local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.solarsystemapp.nasa_api.ApodDataResponse


@Entity("apod")
data class DataApodLocal(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    val date: String,
    val explanation: String,
    val media_type: String,
    val title: String,
    val url: String,
    val hdurl: String?,
    val copyright: String?
)

fun ApodDataResponse.toLocal(): DataApodLocal {
    return DataApodLocal(
        date = date,
        explanation = explanation,
        media_type = media_type,
        title = title,
        url = url,
        hdurl = hdurl,
        copyright = copyright
    )
}

