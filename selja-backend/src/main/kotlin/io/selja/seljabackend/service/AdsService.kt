package io.selja.seljabackend.service

import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.model.Location
import io.selja.seljabackend.model.NewAdItem

interface AdsService {

    fun getAll(location: Location?): List<AdItem>

    fun getOne(id: Long, location: Location?): AdItem

    fun createNewAd(newAdItem: NewAdItem): AdItem

    fun addPhotoToItem(id: Long, url: String): AdItem
}