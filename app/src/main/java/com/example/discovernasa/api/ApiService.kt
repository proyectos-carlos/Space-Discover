package com.example.discovernasa.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// Interface to consume superhero API
interface ApiService {

    // Get an specific body from an ID
    @GET("bodies/{id}")
    suspend fun getBodiesById(@Path("id") bodyId: String) : Response<BodiesDataResponse>

    @GET("bodies/")
    suspend fun getAllBodies() : Response<BodiesDataList>

}