package io.selja.ui.newITem

import android.location.Location
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import io.selja.base.BaseViewModel
import io.selja.base.DeviceId
import io.selja.model.AdItem
import io.selja.model.NewAdItem
import io.selja.repository.AdItemsDataModel
import io.selja.repository.LocationRepository
import io.selja.utils.toHumanReadableString
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class NewItemViewModel(
    private val dataModel: AdItemsDataModel,
    private val locationRepository: LocationRepository,
    private val deviceId: DeviceId
) : BaseViewModel() {

    val newItemLiveData = MutableLiveData<AdItem>()
    val maxDuration = 7 // days
    val location = ObservableField<String>()

    var lastLocation: Location? = null
    var photoFileUri = ObservableField<String>("")
    var absolutePath: String? = null

    init {
        locationRepository.locationListener = { l ->
            lastLocation = l
            location.set(l.toHumanReadableString())
        }
    }

    override fun onAttached() {
        lastLocation = locationRepository.getLastKnownLocation()
        if (lastLocation == null) {
            locationRepository.startLocationUpdates()
            return
        }

        location.set(lastLocation?.toHumanReadableString())
    }

    override fun onDetached() {
        locationRepository.stopLocationUpdates()
    }

    fun save(name: String, desc: String, contact: String, price: Double, itemPosition: Int) =
        coroutineScope.launch {
            loading.set(true)

            val notNullLocation = checkNotNull(lastLocation) { "Location cannot be unknown" }

            val newItem = NewAdItem(
                name = name,
                description = desc,
                phone = contact,
                price = price,
                durationMs = TimeUnit.DAYS.toMillis((itemPosition + 1).toLong()),
                lat = notNullLocation.latitude,
                long = notNullLocation.longitude,
                deviceId = deviceId.getDeviceId()
            )


            val addedItem = withContext(backgroundDispatcher) { dataModel.createNew(newItem, absolutePath) }
            newItemLiveData.value = addedItem
            loading.set(false)
        }

    fun onPhotoFileCreated(photoFileUri: String, absolutePath: String) {
        this.photoFileUri.set(photoFileUri)
        this.absolutePath = absolutePath
    }
}