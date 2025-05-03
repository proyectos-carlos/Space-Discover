package com.example.solarsystemapp.wikipedia_api

import android.util.Log
import com.example.solarsystemapp.Tools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
Wikipedia network singleton
This will provide information such as image of the bodies as well as a description
 */

object WikipediaNetwork{

     private val retrofit : Retrofit by lazy {
            Retrofit.Builder()
            .baseUrl(Tools.BASE_URL_WIKIPEDIA)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val wikipediaApi: WikipediaApiService by lazy {
        retrofit.create(WikipediaApiService::class.java)
    }

    suspend fun searchWikipediaArticle(query : String) : WikipediaSummaryResponse?{
        return withContext(Dispatchers.IO){

            val myResponse = wikipediaApi.getSummary(query)

            if(!myResponse.isSuccessful || myResponse.body() == null){
                Log.i("BigoWikipedia", "La respuesta no funciona ${myResponse.body()}")
                return@withContext null
            }

            val bodiesResult = myResponse.body()!!

            Log.i("BigoWikipedia", "El resultado es $bodiesResult")
            if (bodiesResult.thumbnail == null && bodiesResult.title.isBlank()) {
                Log.i("BigoWikipedia", "No se encontró el artículo de wikipedia para $query")
                return@withContext null
            }

            return@withContext bodiesResult
        }
    }


}