package com.example.discovernasa.solar_system_api

import com.google.gson.annotations.SerializedName

//API response classes for general purpose
data class BodiesDataResponse(
    @SerializedName("id") val id : String,
    @SerializedName("englishName") val englishName : String,
    @SerializedName("discoveryDate") val discoveryDate : String,
    @SerializedName("bodyType") val bodyType : String,
){
    val bodyTypeEnum: BodyType
        get() = BodyType.fromString(bodyType)
}


//API response for detail purpose
data class DetailBodiesDataResponse(
    @SerializedName("id") val id : String,
    @SerializedName("englishName") val englishName : String,
    @SerializedName("gravity") val gravity : Double,
    @SerializedName("discoveredBy") val discoveredBy : String,
    @SerializedName("discoveryDate") val discoveryDate : String,
    @SerializedName("avgTemp") val avgTemp : Int,
    @SerializedName("bodyType") val bodyType : String,
    val imageURL : String? = null, //Extra param1 to wrap with wikipedia
    val description : String? = null //Extra param2 to wrap with wikipedia
){
    val bodyTypeEnum: BodyType
        get() = BodyType.fromString(bodyType)
}


enum class BodyType {
        PLANET,
        MOON,
        ASTEROID,
        COMET,
        STAR,
        DWARF_PLANET,
        UNKNOWN;

        companion object {
            fun fromString(type: String?): BodyType {
                return when (type) {
                    "Planet" -> PLANET
                    "Moon" -> MOON
                    "Asteroid" -> ASTEROID
                    "Comet" -> COMET
                    "Star" -> STAR
                    "Dwarf Planet" -> DWARF_PLANET
                    else -> UNKNOWN
                }
            }
        }
}

data class BodiesDataList(
    @SerializedName("bodies") val bodies : List<BodiesDataResponse>
)
