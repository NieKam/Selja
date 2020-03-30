package io.selja.seljabackend.service

import io.selja.seljabackend.domain.AdItem
import io.selja.seljabackend.domain.Location

interface AdsService {

    fun findAll(location: Location?): List<AdItem>

    fun findOne(id: Long, location: Location?): AdItem

    fun save(adItem: AdItem): AdItem
}