package com.example.solarsystemapp.nasa_api

import com.google.gson.annotations.SerializedName

/*
APOD stands for -> ASTRONOMIC PICTURE OF THE DAY
 */
data class ApodDataResponse(
    @SerializedName("date") val date: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("media_type") val media_type: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("hdurl") val hdurl: String?,
    @SerializedName("copyright") val copyright: String?
)

