package io.selja.seljabackend.service

import io.selja.seljabackend.controller.RADIUS_KM
import io.selja.seljabackend.exception.AdNotFoundException
import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.model.Location
import io.selja.seljabackend.model.getDistanceTo
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
        val allItems = repository.findAllInArea(location.lat, location.long, RADIUS_KM).filter { it.validUntilMs >= now }
        allItems.forEach {
            it.distanceInKm = requireNotNull(it.location).getDistanceTo(location)
        }

        return allItems
    }

    override fun getOne(id: Long, location: Location?): AdItem {
        val optional = repository.findById(id)
        if (!optional.isPresent || optional.get().validUntilMs < now()) {
            throw AdNotFoundException(id)
        }

        val adItem = optional.get()
        location?.let {
            adItem.distanceInKm = requireNotNull(adItem.location).getDistanceTo(location)
        }
        return adItem
    }

    override fun saveNewAd(adItem: AdItem): AdItem {
        return repository.save(adItem)
    }

    private fun now() = System.currentTimeMillis()
}