package com.example.solarsystemapp.solar_system_local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.solarsystemapp.solar_system_api.BodiesDataResponse
import com.example.solarsystemapp.solar_system_api.BodyType
import com.example.solarsystemapp.solar_system_api.DetailBodiesDataResponse


@Entity(tableName = "body")
data class DataBodyLocal(
    @PrimaryKey val id: String,
    val englishName: String,
    val discoveredBy: String,
    val discoveryDate: String,
    val avgTemp: Int,
    val bodyType: String,
    val density: Double,
    val gravity: Double,
    val escape: Double,
    val meanRadius: Double,
    val semimajorAxis: Double,
    val sideralOrbit: Double,
    val sideralRotation: Double,
    val polarRadius: Double,
    val aphelion: Double,
    //val moons: List<Moon>?,          // Guardado como JSON usando TypeConverter
    val imageUrl: String?,           // Extra: de Wikipedia
    val description: String?         // Extra: de Wikipedia
){
    val bodyTypeEnum: BodyType
        get() = BodyType.fromString(bodyType)
}

//Extra function to convert from API to Local data-class
fun DetailBodiesDataResponse.toLocal(): DataBodyLocal {
    return DataBodyLocal(
        id = id,
        englishName = englishName,
        discoveredBy = discoveredBy,
        discoveryDate = discoveryDate,
        avgTemp = avgTemp,
        bodyType = bodyType,
        density = density,
        gravity = gravity,
        escape = escape,
        meanRadius = meanRadius,
        semimajorAxis = semimajorAxis,
        sideralOrbit = sideralOrbit,
        sideralRotation = sideralRotation,
        polarRadius = polarRadius,
        aphelion = aphelion,
        //moons = moons,
        imageUrl = imageURL,
        description = description
    )
}

//Extra function to convert from Local to API data-class
fun DataBodyLocal.toApi(): DetailBodiesDataResponse {
    return DetailBodiesDataResponse(
        id = id,
        englishName = englishName,
        discoveredBy = discoveredBy,
        discoveryDate = discoveryDate,
        avgTemp = avgTemp,
        bodyType = bodyType,
        density = density,
        gravity = gravity,
        escape = escape,
        meanRadius = meanRadius,
        semimajorAxis = semimajorAxis,
        sideralOrbit = sideralOrbit,
        sideralRotation = sideralRotation,
        polarRadius = polarRadius,
        aphelion = aphelion,
        moons = null, // <-- Opcional: Puedes agregar moons si luego lo quieres recuperar de Room
        imageURL = imageUrl,
        description = description
    )
}

//Temporal function to convert from Local to API data-class
fun DataBodyLocal.toBodyDataResponse() : BodiesDataResponse {
    return BodiesDataResponse(
        id = id,
        englishName = englishName,
        discoveryDate = discoveryDate,
        bodyType = bodyType)
}







/*
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

 */
