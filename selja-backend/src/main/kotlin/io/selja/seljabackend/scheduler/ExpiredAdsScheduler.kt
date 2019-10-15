package io.selja.seljabackend.scheduler

import io.selja.seljabackend.repository.AdsRepository
import io.selja.seljabackend.service.StorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private const val HOUR: Long = 60 * 60 * 1000

@Component
class ExpiredAdsScheduler {
    @Autowired
    lateinit var repository: AdsRepository

    @Autowired
    lateinit var storageService: StorageService

    private val logger = LoggerFactory.getLogger(ExpiredAdsScheduler::class.java)

    @Scheduled(fixedRate = HOUR, initialDelay = HOUR)
    fun clearExpiredAds() {
        val expiredAds = repository.findAllOlderThan(System.currentTimeMillis())
        expiredAds.forEach { item ->
            storageService.delete(item.photoUrl)
        }
        repository.deleteAll(expiredAds)
        logger.info("Clear expired ${expiredAds.size} ads")
    }
}