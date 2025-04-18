package com.example.discovernasa.solar_system_api



import android.util.Log
import com.example.discovernasa.Tools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SolarSystemNetwork {

    private fun getRetrofit() : Retrofit {
        val retrofitConfig = Retrofit.Builder()
            .baseUrl(Tools.BASE_URL_SOLAR_SYSTEM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofitConfig
    }

     suspend fun searchBodyById(query : String) : List<BodiesDataResponse>?{
        return withContext(Dispatchers.IO){

            val myResponse = getRetrofit().create(SolarSystemApiService::class.java).getBodiesById(query)

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

            return@withContext listOf(bodiesResult)
        }
    }


     suspend fun getAllBodies() : List<BodiesDataResponse>?{
        return withContext(Dispatchers.IO){
            val myResponse = getRetrofit().create(SolarSystemApiService::class.java).getAllBodies()
            if(!myResponse.isSuccessful || myResponse.body() == null){
                Log.i("BigoReport", "La respuesta no funciona ${myResponse.body()}")
                return@withContext null
            }
            return@withContext myResponse.body()!!.bodies
        }
    }
}
