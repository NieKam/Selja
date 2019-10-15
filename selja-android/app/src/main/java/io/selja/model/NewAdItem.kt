package io.selja.model

data class NewAdItem(
    val deviceId: String,
    val name: String,
    val description: String,
    val phone: String,
    val price: Double,
    val validFor: Long,
    val lat: Double,
    val long: Double
)