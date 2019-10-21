package io.selja.repository

import android.location.Location
import io.selja.model.AdItem
import io.selja.model.NewAdItem


interface AdItemsDataModel {
    suspend fun getAll(location: Location? = null): List<AdItem>

    suspend fun getOne(id: Long, location: Location? = null): AdItem

    suspend fun createNew(newAdItem: NewAdItem, path: String? = null): AdItem
}