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
                Log.i("BigoSolarSystem", "La respuesta no funciona ${myResponse.body()}")
                return@withContext null
            }

            val bodiesResult = myResponse.body()!!

            Log.i("BigoSolarSystem", "El resultado es $bodiesResult")
            if (bodiesResult.englishName.isBlank() && bodiesResult.bodyType.isBlank()) {
                Log.i("BigoSolarSystem", "El cuerpo no se encontró, probablemente el ID es inválido")
                return@withContext null
            }



            return@withContext listOf(bodiesResult)
        }
    }


     suspend fun getAllBodies() : List<BodiesDataResponse>?{
        return withContext(Dispatchers.IO){
            val myResponse = solarSystemApi.getAllBodies()
            if(!myResponse.isSuccessful || myResponse.body() == null){
                Log.i("BigoSolarSystem", "La respuesta no funciona ${myResponse.body()}")
                return@withContext null
            }

            val bodiesResult = myResponse.body()!!.bodies

            return@withContext bodiesResult
        }
    }

    suspend fun getDetailBodyById(query : String) : DetailBodiesDataResponse?{

        return withContext(Dispatchers.IO){

            val myResponse = solarSystemApi.getDetailedBodyById(query)

            if(!myResponse.isSuccessful || myResponse.body() == null){
                Log.i("BigoSolarSystem", "La respuesta no funciona ${myResponse.body()}")
                return@withContext null
            }

            val bodyResult = setupBodyWithWikipedia(myResponse.body()!!)
            Log.i("BigoSolarSystem", "El resultado es $bodyResult")

            if (bodyResult.englishName.isBlank() && bodyResult.bodyType.isBlank()) {
                Log.i("BigoSolarSystem", "El cuerpo no se encontró, probablemente el ID es inválido")
                return@withContext null
            }


            return@withContext bodyResult
        }
    }

    //Override a body with wikipedia data
    private suspend fun setupBodyWithWikipedia(originalBody : DetailBodiesDataResponse) :DetailBodiesDataResponse {
                // Search with bodyType to avoid errors with some bodies I.E: Kale moon
                val wikiData = WikipediaNetwork
                    .searchWikipediaArticle(originalBody.englishName)

                return wikiData?.let { wiki ->
                    originalBody.copy(
                        imageURL = wiki.thumbnail?.source ?: "",
                        description = wiki.extract
                    )
                } ?: originalBody
    }


    //Override the bodies with wikipedia data (URL image and description) if they are not null. (May take a while for all)
    private suspend fun setupBodiesWithWikipedia(originalBodies : List<DetailBodiesDataResponse>): List<DetailBodiesDataResponse> =
        coroutineScope {
            originalBodies.map { body ->
                async {
                    val wikiData = WikipediaNetwork.searchWikipediaArticle(body.englishName)
                    wikiData?.let { wiki ->
                        body.copy(
                            imageURL = wiki.thumbnail?.source ?: "",
                            description = wiki.extract
                        )
                    } ?: body
                }
            }.awaitAll()
        }
}
