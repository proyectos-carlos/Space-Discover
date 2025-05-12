package com.example.solarsystemapp.solar_system_api



import android.util.Log
import com.example.solarsystemapp.Tools
import com.example.solarsystemapp.wikipedia_api.WikipediaNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

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


     suspend fun searchBodyById(query: String): BodiesDataResponse? = withContext(Dispatchers.IO) {

    try {
        val myResponse = solarSystemApi.getBodiesById(query)

        if (!myResponse.isSuccessful || myResponse.body() == null) {
            Log.i("BigoSolarSystem", "La respuesta no funciona ${myResponse.body()}")
            return@withContext null
        }

        val bodiesResult = myResponse.body()!!

        Log.i("BigoSolarSystem", "El resultado es $bodiesResult")
        if (bodiesResult.englishName.isBlank() && bodiesResult.bodyType.isBlank()) {
            Log.i("BigoSolarSystem", "El cuerpo no se encontró, probablemente el ID es inválido")
            return@withContext null
        }
        return@withContext bodiesResult
    }catch (e : IOException){
        Log.i("BigoSolarSystem", "La respuesta no funciona ${e.message} causa: ${e.cause}")
        return@withContext null
    }catch (e : Exception){
        Log.i("BigoSolarSystem", "La respuesta no funciona ${e.message} causa: ${e.cause}")
        return@withContext null
    }

    }


    suspend fun getAllBodies(): List<BodiesDataResponse>? = withContext(Dispatchers.IO) {
        try {
            val myResponse = solarSystemApi.getAllBodies()
            if (!myResponse.isSuccessful || myResponse.body() == null) {
                Log.i("BigoSolarSystem", "La respuesta no funciona ${myResponse.code()}")
                return@withContext null
            }

            val bodiesResult = myResponse.body()!!.bodies

            return@withContext bodiesResult
        }catch (e : Exception){
            Log.i("BigoSolarSystem", "La respuesta no funciona ${e.message} causa: ${e.cause}")
            return@withContext null
        }
    }

    suspend fun searchBodiesByName(query: String): List<BodiesDataResponse> = withContext(Dispatchers.IO) {
        val bodies = getAllBodies()
        bodies?.let { allBodies ->
            return@withContext allBodies.filter { body -> body.englishName.contains(query, ignoreCase = true) }
        } ?: run{
            return@withContext emptyList()
        }
    }

    suspend fun getDetailBodyById(query: String): DetailBodiesDataResponse? =
        withContext(Dispatchers.IO) {
            try {

                val myResponse = solarSystemApi.getDetailedBodyById(query)

                if (!myResponse.isSuccessful || myResponse.body() == null) {
                    Log.i("BigoSolarSystem", "La respuesta no funciona ${myResponse.body()}")
                    return@withContext null
                }

                val bodyResult = setupBodyWithWikipedia(myResponse.body()!!)
                Log.i("BigoSolarSystem", "El resultado es $bodyResult")

                if (bodyResult.englishName.isBlank() && bodyResult.bodyType.isBlank()) {
                    Log.i(
                        "BigoSolarSystem",
                        "El cuerpo no se encontró, probablemente el ID es inválido"
                    )
                    return@withContext null
                }


                return@withContext bodyResult
            }catch (e : Exception){
                Log.i("BigoSolarSystem", "La respuesta no funciona ${e.message} causa: ${e.cause}")
                return@withContext null
            }
        }


    // Get a body given a full URL from API. I.E: https://api.le-systeme-solaire.net/rest/bodies/ariel => ariel
    // (typically used to get moons from a body, parameter rel contains full API URL)
    private suspend fun getBodyByFullUrl(query: String): BodiesDataResponse? =
        withContext(Dispatchers.IO) {
            val id = query.substringAfterLast("/")
            return@withContext searchBodyById(id)
        }

    suspend fun getAllMoons(moons: List<Moon>): List<BodiesDataResponse> = coroutineScope {
        moons.map { moon ->
            async {
                getBodyByFullUrl(moon.rel)
            }
        }.awaitAll()
            .filterNotNull()
    }



    //Override a body with wikipedia data
    private suspend fun setupBodyWithWikipedia(originalBody: DetailBodiesDataResponse): DetailBodiesDataResponse {
        // Search with bodyType and discard refer to, refers to
        val wikiData = WikipediaNetwork
            .searchWikipediaArticle(originalBody.englishName)

        return wikiData?.let { wiki ->
            val isDisambiguation = wiki.extract.contains("refer to", ignoreCase = true)
                    || wiki.extract.contains("refers to", ignoreCase = true)

            originalBody.copy(
                imageURL = wiki.thumbnail?.source ?: "",
                description = if(isDisambiguation) null else wiki.extract
            )
        } ?: originalBody
    }


    //Override the bodies with wikipedia data (URL image and description) if they are not null. (May take a while for all)
    private suspend fun setupBodiesWithWikipedia(originalBodies: List<DetailBodiesDataResponse>): List<DetailBodiesDataResponse> =
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

