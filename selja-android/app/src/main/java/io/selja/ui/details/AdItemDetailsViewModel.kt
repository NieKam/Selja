package io.selja.ui.details

import android.location.Location
import androidx.databinding.ObservableField
import io.selja.base.BaseViewModel
import io.selja.base.DeviceId
import io.selja.model.AdItem
import io.selja.model.getPriceFormatted
import io.selja.model.imageUrlWithHost
import io.selja.repository.AdItemsDataModel
import io.selja.repository.LocationRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdItemDetailsViewModel(
    private val dataModel: AdItemsDataModel,
    private val locationRepository: LocationRepository,
    private val deviceId: DeviceId
) : BaseViewModel() {

    val detailedView = true
    val oAdItem = ObservableField<AdItem>()
    val phoneNumber = ObservableField<String>()

    private var lastSavedLocation: Location? = null

    init {
        locationRepository.locationListener = { location ->
            lastSavedLocation = location
            loadAdItem()
        }
    }

    fun getPrice() = oAdItem.get()?.getPriceFormatted()
    fun getImageUrl() = oAdItem.get()?.imageUrlWithHost()
    fun isMine() = oAdItem.get()?.deviceId == deviceId.getDeviceId()

    override fun onAttached() {
        loading.set(true)
        lastSavedLocation = locationRepository.getLastKnownLocation()
        if (lastSavedLocation == null) {
            locationRepository.startLocationUpdates()
        } else {
            loadAdItem()
        }
    }

    override fun onDetached() {
        locationRepository.stopLocationUpdates()
    }

    fun refresh() {
        loading.set(true)
        loadAdItem()
    }

    private fun loadAdItem() = coroutineScope.launch {
        val item = withContext(backgroundDispatcher) {
            dataModel.getOne(requireNotNull(oAdItem.get()).id, lastSavedLocation)
        }

        oAdItem.set(item)
        phoneNumber.set(item.phoneObfuscated)
        loading.set(false)
    }

    fun revealPhone() {
        phoneNumber.set(oAdItem.get()?.phone)
    }
}