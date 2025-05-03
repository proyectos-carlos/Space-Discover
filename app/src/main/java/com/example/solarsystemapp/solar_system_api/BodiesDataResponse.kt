package com.example.solarsystemapp.solar_system_api

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
    @SerializedName("discoveredBy") val discoveredBy : String,
    @SerializedName("discoveryDate") val discoveryDate : String,
    @SerializedName("avgTemp") val avgTemp : Int,
    @SerializedName("bodyType") val bodyType : String,
    @SerializedName("density") val density : Double,
    @SerializedName("gravity") val gravity : Double,
    @SerializedName("escape") val escape : Double,
    @SerializedName("meanRadius") val meanRadius : Double,
    @SerializedName("semimajorAxis") val semimajorAxis : Double,
    @SerializedName("sideralOrbit") val sideralOrbit : Double,
    @SerializedName("sideralRotation") val sideralRotation : Double,
    @SerializedName("polarRadius") val polarRadius : Double,
    @SerializedName("aphelion") val aphelion : Double,
    @SerializedName("moons") val moons : List<Moon>?,
    val imageURL : String? = null, //Extra param1 to wrap with wikipedia
    val description : String? = null //Extra param2 to wrap with wikipedia
){
    val bodyTypeEnum: BodyType
        get() = BodyType.fromString(bodyType)
}

data class Moon(
    @SerializedName("moon") val moon : String,
    @SerializedName("rel") val rel : String
)


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
