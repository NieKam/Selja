package io.selja.seljabackend.service

import io.selja.seljabackend.configuration.RADIUS_KM
import io.selja.seljabackend.web.rest.errors.AdNotFoundException
import io.selja.seljabackend.domain.AdItem
import io.selja.seljabackend.domain.Location
import io.selja.seljabackend.domain.getDistanceTo
import io.selja.seljabackend.repository.AdsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdsServiceImpl : AdsService {

    @Autowired
    lateinit var repository: AdsRepository

    override fun findAll(location: Location?): List<AdItem> {
        val now = now()
        if (location == null) {
            return repository.findAll().filter { it.validUntilMs >= now }
        }
        val allItems = repository.findAllInArea(location.lat, location.long, RADIUS_KM, now)
        allItems.forEach {
            it.distanceInKm = requireNotNull(it.location).getDistanceTo(location)
        }

        return allItems
    }

    override fun findOne(id: Long, location: Location?): AdItem {
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

    override fun save(adItem: AdItem): AdItem {
        return repository.save(adItem)
    }

    private fun now() = System.currentTimeMillis()
}