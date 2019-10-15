package io.selja.model

import android.os.Parcelable
import com.squareup.moshi.Json
import io.selja.BuildConfig
import kotlinx.android.parcel.Parcelize

const val PARCEL_PARAM = "parcel"

@Parcelize
data class AdItem(
    @field:Json(name = "id")
    val id: Long = 0,

    @field:Json(name = "deviceId")
    val deviceId: String = "",

    @field:Json(name = "name")
    val name: String = "",

    @field:Json(name = "description")
    val description: String = "",

    @field:Json(name = "photoUrl")
    val imageUrl: String = "",

    @field:Json(name = "phone")
    val phone: String = "",

    @field:Json(name = "phoneObfuscated")
    val phoneObfuscated: String = "",

    @field:Json(name = "price")
    val price: Double = 0.0,

    @field:Json(name = "currency")
    val currency: String = "",

    @field:Json(name = "distanceInKm")
    var distanceInKm: Double = 0.0,

    @field:Json(name = "validUntil")
    val validUntil: Long = 0
) : Parcelable

fun AdItem.getPriceFormatted() = "${this.price} ${this.currency}"

fun AdItem.imageUrlWithHost() = "${BuildConfig.BASE_URL}${this.imageUrl}"