package com.example.discovernasa.wikipedia_api

import com.google.gson.annotations.SerializedName



data class WikipediaSummaryResponse(
    @SerializedName("title") val title: String,

    @SerializedName("extract") val extract: String,

    @SerializedName("thumbnail") val thumbnail: Thumbnail? // puede no existir
)

data class Thumbnail(
    @SerializedName("source") val source: String,

)


