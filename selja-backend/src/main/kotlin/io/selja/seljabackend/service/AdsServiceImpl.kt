package io.selja.seljabackend.service

import io.selja.seljabackend.controller.RADIUS_KM
import io.selja.seljabackend.exception.AdNotFoundException
import io.selja.seljabackend.model.*
import io.selja.seljabackend.repository.AdsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdsServiceImpl : AdsService {
    @Autowired
    lateinit var repository: AdsRepository

    override fun getAll(location: Location?): List<AdItem> {
        if (location == null) {
            return repository.findAll()
        }

        val now = now()
        val allItems = repository.findAllInArea(location.lat, location.long, RADIUS_KM).filter { it.validUntil >= now }
        allItems.forEach {
            it.distanceInKm = requireNotNull(it.location).getDistanceTo(location)
        }

        return allItems
    }

    override fun getOne(id: Long, location: Location?): AdItem {
        val optional = repository.findById(id)
        if (!optional.isPresent || optional.get().validUntil < now()) {
            throw AdNotFoundException(id)
        }

        val adItem = optional.get()
        location?.let {
            adItem.distanceInKm = requireNotNull(adItem.location).getDistanceTo(location)
        }
        return adItem
    }

    override fun createNewAd(newAdItem: NewAdItem): AdItem {
        return repository.save(newAdItem.toAdItem())
    }

    override fun addPhotoToItem(id: Long, url: String): AdItem {
        val optional = repository.findById(id)
        if (!optional.isPresent) {
            throw AdNotFoundException(id)
        }

        val adItem = optional.get()
        if (adItem.photoUrl.isNotEmpty()) {
            // Prevent overwriting images
            return adItem
        }

        adItem.photoUrl = url
        repository.save(adItem)
        return adItem
    }

    private fun now() = System.currentTimeMillis()
}