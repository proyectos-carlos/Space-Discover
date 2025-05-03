package com.example.solarsystemapp.nasa_api

import com.example.solarsystemapp.Tools
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/*
APOD stands for -> ASTRONOMIC PICTURE OF THE DAY
 */

interface ApodApiService {

    @GET("planetary/apod")
    suspend fun getAstronomicPictureOfTheDay(
        @Query("api_key") apiKey: String = Tools.NASA_API_KEY,
        @Query("date") date: String? = null
    ): Response<ApodDataResponse>


}