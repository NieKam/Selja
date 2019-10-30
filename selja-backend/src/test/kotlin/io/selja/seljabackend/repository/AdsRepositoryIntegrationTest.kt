package io.selja.seljabackend.repository

import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.model.Location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.TimeUnit

@DataJpaTest
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdsRepositoryIntegrationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var adsRepository: AdsRepository

    private val now = System.currentTimeMillis()

    private val tomorrow = now + TimeUnit.DAYS.toMillis(1)

    @Test
    fun whenFindOlderThan_thenReturnAd() {
        // given
        val location = Location(lat = 0.0, long = 0.0)
        val ad = AdItem(validUntilMs = now, location = location)

        //entityManager.persist(location)
        entityManager.persist(ad)
        entityManager.flush()

        // when
        val found = adsRepository.findAllOlderThan(tomorrow)

        // then
        assertThat(found.size).isEqualTo(1)
        assertThat(found[0].id).isEqualTo(ad.id)
    }

    @Test
    fun whenFindNotOlderThan_thenReturnNothing() {
        // given
        val now = System.currentTimeMillis()
        val location = Location(lat = 0.0, long = 0.0)
        val ad = AdItem(validUntilMs = tomorrow, location = location)

        entityManager.persist(ad)
        entityManager.flush()

        // when
        val found = adsRepository.findAllOlderThan(now)

        // then
        assertThat(found.size).isEqualTo(0)
    }

    @Test
    fun whenInRange_thenReturnAds() {
        // given
        val adInRange1 = AdItem(location = Location(lat = 50.04, long = 10.0))
        val adInRange2 = AdItem(location = Location(lat = 50.01, long = 10.0))
        val adFarAway = AdItem(location = Location(lat = 20.2, long = 10.0))

        //entityManager.persist(location)
        entityManager.persist(adInRange1)
        entityManager.persist(adInRange2)
        entityManager.persist(adFarAway)
        entityManager.flush()

        // when
        val found = adsRepository.findAllInArea(lat = 50.0, long = 10.0, radiusKm = 10.0)

        // then
        assertThat(found.size).isEqualTo(2)
        assertThat(found[0].id).isEqualTo(adInRange2.id)
        assertThat(found[1].id).isEqualTo(adInRange1.id)
    }
}