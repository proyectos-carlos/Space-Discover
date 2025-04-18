package com.example.discovernasa.api

import com.google.gson.annotations.SerializedName

data class BodiesDataResponse(
    @SerializedName("id") val id : String,
    @SerializedName("englishName") val englishName : String,
    @SerializedName("gravity") val gravity : Double,
    @SerializedName("discoveredBy") val discoveredBy : String,
    @SerializedName("discoveryDate") val discoveryDate : String,
    @SerializedName("avgTemp") val avgTemp : Int,
    @SerializedName("bodyType") val bodyType : String,
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
