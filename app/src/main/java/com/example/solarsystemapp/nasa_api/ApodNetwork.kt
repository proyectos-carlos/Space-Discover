package com.example.solarsystemapp.nasa_api

import android.util.Log
import com.example.solarsystemapp.Tools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/*
APOD stands for -> ASTRONOMIC PICTURE OF THE DAY
 */

object ApodNetwork {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Tools.BASE_URL_NASA)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apodApi: ApodApiService by lazy {
        retrofit.create(ApodApiService::class.java)
    }


    suspend fun getApod(date: String?): ApodDataResponse? = withContext(Dispatchers.IO) {

        try {
            val myResponse = apodApi.getAstronomicPictureOfTheDay(date = date)

            if (!myResponse.isSuccessful || myResponse.body() == null) {
                Log.i(
                    "BigoNASA", "La respuesta no funciona. Código HTTP: ${myResponse.code()}, " +
                            "Error: ${myResponse.errorBody()?.string()}"
                )
                return@withContext null
            }

            val apodResult = myResponse.body()!!

            Log.i("BigoNASA", "El resultado es $apodResult")
            if (apodResult.title.isBlank() || apodResult.url.isBlank()) {
                Log.i("BigoNASA", "No tiene parámetros mínimos (título o la URL)")
                return@withContext null
            }
            return@withContext apodResult
        }catch(e : IOException){ // Usually a network error. User has no internet
            Log.i("BigoNASA", "La respuesta no funciona ${e.message} causa: ${e.cause}")
            return@withContext null
        } catch (e: Exception) {
            Log.i("BigoNASA", "La respuesta no funciona ${e.message} causa: ${e.cause}")
            return@withContext null
        }
    }
}