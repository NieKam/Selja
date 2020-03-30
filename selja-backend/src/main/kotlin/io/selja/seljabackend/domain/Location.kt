package io.selja.seljabackend.domain

import io.selja.seljabackend.web.rest.errors.BadLocationException
import java.math.BigDecimal
import java.math.RoundingMode
import javax.persistence.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Entity
@Table(name = "location")
data class Location(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        val lat: Double,
        val long: Double
) {
    companion object {
        fun create(stringLat: String, stringLong: String): Location? {
            if (stringLat.isEmpty() or stringLong.isEmpty()) {
                return null
            }

            val lat: Double
            val long: Double
            try {
                lat = stringLat.toDouble()
                long = stringLong.toDouble()
            } catch (e: NumberFormatException) {
                throw BadLocationException()
            }

            return Location(lat = lat.roundTo(4), long = long.roundTo(4))
        }
    }
}

fun Double.roundTo(decimalPlaces: Int): Double {
    if (decimalPlaces < 0) {
        return this
    }

    return BigDecimal.valueOf(this).setScale(decimalPlaces, RoundingMode.HALF_UP).toDouble()
}

fun Location.getDistanceTo(location: Location): Double {
    val radius = 6371
    val phi1 = Math.toRadians(this.lat)
    val phi2 = Math.toRadians(location.lat)
    val deltaPhi = Math.toRadians(location.lat - this.lat)
    val deltaL = Math.toRadians(location.long - this.long)

    val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
            cos(phi1) * cos(phi2) * sin(deltaL / 2) * sin(deltaL / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return radius * c
}

