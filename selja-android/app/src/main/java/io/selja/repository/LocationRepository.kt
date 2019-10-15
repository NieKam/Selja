package io.selja.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import io.selja.permissions.LOCATION_PERMISSION
import java.util.concurrent.TimeUnit

class LocationRepository(private val context: Context) {
    var locationListener: ((Location) -> Unit)? = null

    private val locationCallback = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            location ?: return
            locationListener?.invoke(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String?) {}

        override fun onProviderDisabled(provider: String?) {}
    }

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(): Location? {
        if (!hasPermission(context)) {
            return null
        }

        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (!hasPermission(context)) {
            locationCallback.onLocationChanged(null)
            return

        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            TimeUnit.MINUTES.toMillis(3),
            500F,
            locationCallback
        )
    }

    fun stopLocationUpdates() {
        locationManager.removeUpdates(locationCallback)
    }

    private fun hasPermission(context: Context) =
        ContextCompat.checkSelfPermission(context, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
}