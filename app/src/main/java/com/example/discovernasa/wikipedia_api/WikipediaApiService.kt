package com.example.discovernasa.wikipedia_api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaApiService {
    @GET("page/summary/{title}")
    suspend fun getSummary(@Path("title") title: String): Response<WikipediaSummaryResponse>

}
