package io.selja.ui.overview

import android.location.Location
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import io.selja.base.BaseViewModel
import io.selja.model.AdItem
import io.selja.repository.AdItemsDataModel
import io.selja.repository.LocationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ItemsOverviewViewModel(private val dataModel: AdItemsDataModel, private val locationRepo: LocationRepository) :
    BaseViewModel() {

    val showEmptyView = ObservableBoolean(false)
    val adItems: MutableLiveData<List<AdItem>> by lazy {
        MutableLiveData<List<AdItem>>()
    }

    private var lastSavedLocation: Location? = null

    init {
        locationRepo.locationListener = { location ->
            lastSavedLocation = location
            getAll()
        }
    }

    override fun initPermissionState(hasLocationPermission: Boolean) {
        super.initPermissionState(hasLocationPermission)
        loading.set(true)
                if (!hasLocationPermission) {
            getAll()
            return
        }

        lastSavedLocation = locationRepo.getLastKnownLocation()
        if (lastSavedLocation == null) {
            locationRepo.startLocationUpdates()
        } else {
            getAll()
        }
    }

    fun refresh() {
        loading.set(true)
        getAll()
    }

    override fun onDetached() {
        locationRepo.stopLocationUpdates()
    }

    fun onRequestPermissionsResult(granted: Boolean) {
        hasLocationPermission.set(granted)
        if (granted) {
            locationRepo.startLocationUpdates()
        }
    }

    fun addNewItem(adItem: AdItem) {
        showEmptyView.set(false)
        val mutableList = requireNotNull(adItems.value).toMutableList().also { it.add(0, adItem) }
        adItems.value = mutableList

    }

    private fun getAll() = coroutineScope.launch {
        val task = async(backgroundDispatcher) {
            dataModel.getAll(lastSavedLocation)
        }
        val items = task.await()
        adItems.value = items
        loading.set(false)
        showEmptyView.set(items.isEmpty())
    }
}