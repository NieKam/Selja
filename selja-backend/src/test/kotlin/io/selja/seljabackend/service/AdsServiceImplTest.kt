package io.selja.seljabackend.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import io.selja.seljabackend.configuration.RADIUS_KM
import io.selja.seljabackend.web.rest.errors.AdNotFoundException
import io.selja.seljabackend.domain.AdItem
import io.selja.seljabackend.domain.Location
import io.selja.seljabackend.domain.NewAdItem
import io.selja.seljabackend.domain.toAdItem
import io.selja.seljabackend.repository.AdsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import java.util.concurrent.TimeUnit

@ExtendWith(SpringExtension::class)
class AdsServiceImplTest {

    @TestConfiguration
    internal class AdsServiceImplTestContextConfiguration {
        @Bean
        fun adsService(): AdsService {
            return AdsServiceImpl()
        }
    }

    @Autowired
    private lateinit var adsService: AdsService

    @MockBean
    private lateinit var adsRepository: AdsRepository

    private val now = System.currentTimeMillis()
    private val tomorrow = now + TimeUnit.DAYS.toMillis(1)
    private val locationA = Location(1, 50.0, 10.0)
    private val locationB = Location(1, 50.05, 10.0)

    @Test
    fun whenAdIsNotAvailable_thenAnExceptionIsThrown() {
        Assertions.assertThrows(AdNotFoundException::class.java) {
            adsService.findOne(1, null)
        }
    }

    @Test
    fun getAllIfLocationIsNull_thenDistanceShouldBeZero() {
        val ad = AdItem(location = Location(1, 50.0, 10.0), name = "Item")
        whenever(adsRepository.findAll()).thenReturn(mutableListOf(ad))

        val found = adsService.findAll(null)

        assertThat(found.size).isEqualTo(1)
        assertThat(found[0].id).isEqualTo(ad.id)
        assertThat(found[0].distanceInKm).isEqualTo(0.0)
    }

    @Test
    fun getAllIfLocationIsNotNull_thenDistanceShouldBeLowerThanRadius() {
        val now = System.currentTimeMillis()
        val ad = AdItem(location = locationA, validUntilMs = now + TimeUnit.DAYS.toMillis(1))
        whenever(adsRepository.findAllInArea(
                lat = locationB.lat,
                long = locationB.long,
                radiusKm = RADIUS_KM,
                timestamp = now)).thenReturn(mutableListOf(ad))

        val found = adsService.findAll(locationB)

        assertThat(found.size).isEqualTo(1)
        assertThat(found[0].id).isEqualTo(ad.id)
        assertThat(found[0].distanceInKm).isGreaterThan(0.0)
        assertThat(found[0].distanceInKm).isLessThan(RADIUS_KM)
    }

    @Test
    fun getOneIfLocationIsNotNull_thenDistanceShouldBeLowerThanRadius() {
        val id = 1L
        val ad = AdItem(location = locationA, name = "Item", validUntilMs = tomorrow)
        whenever(adsRepository.findById(eq(id))).thenReturn(Optional.of(ad))

        val found = adsService.findOne(id, locationB)

        assertThat(found.id).isEqualTo(ad.id)
        assertThat(found.distanceInKm).isGreaterThan(0.0)
        assertThat(found.distanceInKm).isLessThan(RADIUS_KM)
    }

    @Test
    fun getOneIfLocationIsNull_thenDistanceShouldBeZero() {
        val id = 1L
        val ad = AdItem(id = id, location = locationA, name = "Item", validUntilMs = tomorrow)
        whenever(adsRepository.findById(eq(id))).thenReturn(Optional.of(ad))

        val found = adsService.findOne(id, null)

        assertThat(found.id).isEqualTo(ad.id)
        assertThat(found.distanceInKm).isEqualTo(0.0)
    }

    @Test
    fun createOne_thenNewItemShouldBeReturned() {
        val phone = "123 456 789"
        val obfuscatedPhone = "123****9"
        val url = "images/dcxsdf"

        val newItem = NewAdItem(deviceId = "dev-Id", name = "name", description = "desc", phone = phone, price = 10000.5, duration = 60_000, lat = 50.0, long = 10.0)
        val adItem = newItem.toAdItem()
        adItem.photoUrl = url
        whenever(adsRepository.save(any<AdItem>())).thenReturn(adItem)

        val ad = adsService.save(adItem)

        assertThat(ad.deviceId).isEqualTo(newItem.deviceId)
        assertThat(ad.name).isEqualTo(newItem.name)
        assertThat(ad.description).isEqualTo(newItem.description)
        assertThat(ad.phone).isEqualTo(newItem.phone)
        assertThat(ad.price).isEqualTo("10,000.5 zł")
        assertThat(ad.validUntilMs).isGreaterThan(now)
        assertThat(ad.location?.lat).isEqualTo(newItem.lat)
        assertThat(ad.location?.long).isEqualTo(newItem.long)
        assertThat(ad.distanceInKm).isEqualTo(0.0)
        assertThat(ad.phoneObfuscated).isEqualTo(obfuscatedPhone)
        assertThat(ad.photoUrl).isEqualTo(url)
    }

    @Test
    fun `create item without photo`() {
        val newItem = NewAdItem(deviceId = "dev-Id", name = "name", description = "desc", phone = "123", price = 10.0, duration = 60_000, lat = 50.0, long = 10.0)
        val adItem = newItem.toAdItem()
        whenever(adsRepository.save(any<AdItem>())).thenReturn(adItem)

        val ad = adsService.save(adItem)
        assertThat(ad.photoUrl).isEmpty()
    }
}