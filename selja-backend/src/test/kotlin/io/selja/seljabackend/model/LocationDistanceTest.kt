package io.selja.seljabackend.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class LocationDistanceTest {
    @Test
    fun testDistanceCalculations() {
        val locationA = Location(0, 50.0, 10.0)
        val locationB = Location(1, 30.0, 5.0)
        val expected = 2263.0 // calculated on page https://www.movable-type.co.uk/scripts/latlong.html

        val actual = locationA.getDistanceTo(locationB)
        assertThat(expected).isEqualTo(actual, Offset.offset(1.0))
    }
}