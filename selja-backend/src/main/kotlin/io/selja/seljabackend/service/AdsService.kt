package io.selja.seljabackend.service

import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.model.Location

interface AdsService {

    fun getAll(location: Location?): List<AdItem>

    fun getOne(id: Long, location: Location?): AdItem

    fun saveNewAd(adItem: AdItem): AdItem
}