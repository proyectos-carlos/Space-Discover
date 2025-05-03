package com.example.solarsystemapp.solar_system_api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// Interface to consume superhero API
interface SolarSystemApiService {

    // Get an specific body from an ID
    @GET("bodies/{id}")
    suspend fun getBodiesById(@Path("id") bodyId: String) : Response<BodiesDataResponse>

    // Get all the bodies
    @GET("bodies/")
    suspend fun getAllBodies() : Response<BodiesDataList>

    // Get a detailed body from an ID
    @GET("bodies/{id}")
    suspend fun getDetailedBodyById(@Path("id") bodyId: String) : Response<DetailBodiesDataResponse>

}