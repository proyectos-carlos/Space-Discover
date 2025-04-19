package com.example.discovernasa.solar_system_api



import android.util.Log
import com.example.discovernasa.Tools
import com.example.discovernasa.wikipedia_api.WikipediaNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SolarSystemNetwork {


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Tools.BASE_URL_SOLAR_SYSTEM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val solarSystemApi: SolarSystemApiService by lazy {
        retrofit.create(SolarSystemApiService::class.java)
    }

     suspend fun searchBodyById(query : String) : List<BodiesDataResponse>?{
        return withContext(Dispatchers.IO){

            val myResponse = solarSystemApi.getBodiesById(query)

            if(!myResponse.isSuccessful || myResponse.body() == null){
                Log.i("BigoReport", "La respuesta no funciona ${myResponse.body()}")
                return@withContext null
            }

            val bodiesResult = myResponse.body()!!

            Log.i("BigoReport", "El resultado es $bodiesResult")
            if (bodiesResult.englishName.isBlank() && bodiesResult.bodyType.isBlank()) {
                Log.i("BigoReport", "El cuerpo no se encontró, probablemente el ID es inválido")
                return@withContext null
            }



            return@withContext setupBodiesWithWikipedia(listOf(bodiesResult))
        }
    }


     suspend fun getAllBodies() : List<BodiesDataResponse>?{
        return withContext(Dispatchers.IO){
            val myResponse = solarSystemApi.getAllBodies()
            if(!myResponse.isSuccessful || myResponse.body() == null){
                Log.i("BigoReport", "La respuesta no funciona ${myResponse.body()}")
                return@withContext null
            }

            val bodiesResult = myResponse.body()!!.bodies

            return@withContext setupBodiesWithWikipedia(bodiesResult)
        }
    }

    //Override the bodies with wikipedia data (URL image and description) if they are not null


    private suspend fun setupBodiesWithWikipedia(originalBodies : List<BodiesDataResponse>): List<BodiesDataResponse> =
        coroutineScope {
            originalBodies.map { body ->
                async {
                    val wikiData = WikipediaNetwork.searchWikipediaArticle(body.englishName)
                    wikiData?.let {
                        body.copy(
                            imageURL = it.thumbnail?.source ?: "",
                            description = it.extract
                        )
                    } ?: body
                }
            }.awaitAll()
        }
}
