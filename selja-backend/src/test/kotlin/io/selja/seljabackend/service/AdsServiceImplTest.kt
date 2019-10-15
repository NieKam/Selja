package io.selja.seljabackend.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import io.selja.seljabackend.controller.RADIUS_KM
import io.selja.seljabackend.exception.AdNotFoundException
import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.model.Location
import io.selja.seljabackend.model.NewAdItem
import io.selja.seljabackend.model.toAdItem
import io.selja.seljabackend.repository.AdsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
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

    @Test(expected = AdNotFoundException::class)
    fun whenAdIsNotAvailable_thenAnExceptionIsThrown() {
        adsService.getOne(1, null)
    }

    @Test
    fun getAllIfLocationIsNull_thenDistanceShouldBeZero() {
        val ad = AdItem(location = Location(1, 50.0, 10.0), name = "Item")
        whenever(adsRepository.findAll()).thenReturn(mutableListOf(ad))

        val found = adsService.getAll(null)

        assertThat(found.size).isEqualTo(1)
        assertThat(found[0].id).isEqualTo(ad.id)
        assertThat(found[0].distanceInKm).isEqualTo(0.0)
    }

    @Test
    fun getAllIfLocationIsNotNull_thenDistanceShouldBeLowerThanRadius() {

        val ad = AdItem(location = locationA, validUntil = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        whenever(adsRepository.findAllInArea(lat = locationB.lat, long = locationB.long, radiusKm = RADIUS_KM)).thenReturn(mutableListOf(ad))

        val found = adsService.getAll(locationB)

        assertThat(found.size).isEqualTo(1)
        assertThat(found[0].id).isEqualTo(ad.id)
        assertThat(found[0].distanceInKm).isGreaterThan(0.0)
        assertThat(found[0].distanceInKm).isLessThan(RADIUS_KM)
    }

    @Test
    fun getOneIfLocationIsNotNull_thenDistanceShouldBeLowerThanRadius() {
        val id = 1L
        val ad = AdItem(location = locationA, name = "Item", validUntil = tomorrow)
        whenever(adsRepository.findById(eq(id))).thenReturn(Optional.of(ad))

        val found = adsService.getOne(id, locationB)

        assertThat(found.id).isEqualTo(ad.id)
        assertThat(found.distanceInKm).isGreaterThan(0.0)
        assertThat(found.distanceInKm).isLessThan(RADIUS_KM)
    }

    @Test
    fun getOneIfLocationIsNull_thenDistanceShouldBeZero() {
        val id = 1L
        val ad = AdItem(id = id, location = locationA, name = "Item", validUntil = tomorrow)
        whenever(adsRepository.findById(eq(id))).thenReturn(Optional.of(ad))

        val found = adsService.getOne(id, null)

        assertThat(found.id).isEqualTo(ad.id)
        assertThat(found.distanceInKm).isEqualTo(0.0)
    }

    @Test
    fun createOne_thenNewItemShouldBeReturned() {
        val phone = "123 456 789"
        val obfuscatedPhone = "123****9"

        val newItem = NewAdItem(deviceId = "dev-Id", name = "name", description = "desc", phone = phone, price = 10.0, validFor = 60_000, lat = 50.0, long = 10.0)
        val adItem = newItem.toAdItem()
        whenever(adsRepository.save(any<AdItem>())).thenReturn(adItem)

        val ad = adsService.createNewAd(newItem)

        assertThat(ad.deviceId).isEqualTo(newItem.deviceId)
        assertThat(ad.name).isEqualTo(newItem.name)
        assertThat(ad.description).isEqualTo(newItem.description)
        assertThat(ad.phone).isEqualTo(newItem.phone)
        assertThat(ad.price).isEqualTo(newItem.price)
        assertThat(ad.validUntil).isGreaterThan(now)
        assertThat(ad.location?.lat).isEqualTo(newItem.lat)
        assertThat(ad.location?.long).isEqualTo(newItem.long)
        assertThat(ad.distanceInKm).isEqualTo(0.0)
        assertThat(ad.phoneObfuscated).isEqualTo(obfuscatedPhone)
        assertThat(ad.photoUrl).isEmpty()
    }

    @Test
    fun uploadPhoto_thenItemShouldBeUpdated() {
        val id = 1L
        val url = "images/dcxsdf"

        val ad = AdItem(id = id, validUntil = tomorrow)
        whenever(adsRepository.findById(eq(id))).thenReturn(Optional.of(ad))

        val updatedItem = adsService.addPhotoToItem(id, url)
        assertThat(updatedItem.photoUrl).isEqualTo(url)
    }

    @Test(expected = AdNotFoundException::class)
    fun whenUploadPhotoWithWrongId_thenAnExceptionIsThrown() {
        adsService.addPhotoToItem(1, "url")
    }
}