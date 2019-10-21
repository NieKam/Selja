package io.selja.model

import com.squareup.moshi.Json

data class NewAdItem(
    @field:Json(name = "deviceId")
    val deviceId: String,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "description")
    val description: String,
    @field:Json(name = "phone")
    val phone: String,
    @field:Json(name = "price")
    val price: Double,
    @field:Json(name = "duration")
    val durationMs: Long,
    @field:Json(name = "lat")
    val lat: Double,
    @field:Json(name = "long")
    val long: Double
)