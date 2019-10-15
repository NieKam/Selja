package io.selja.seljabackend.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import io.selja.seljabackend.controller.CURRENCY
import io.selja.seljabackend.views.Views
import javax.persistence.*

@Entity
@Table(name = "ads")
data class AdItem(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @JsonView(Views.Short::class)
        val id: Long = 0,

        @JsonView(Views.Short::class)
        val deviceId: String = "",

        @JsonView(Views.Short::class)
        val name: String = "",

        @JsonView(Views.Full::class)
        val description: String = "",

        @JsonView(Views.Full::class)
        val phone: String = "",

        @JsonView(Views.Short::class)
        val phoneObfuscated: String = "",

        @JsonView(Views.Short::class)
        var photoUrl: String = "",

        @JsonView(Views.Short::class)
        val price: Double = 0.0,

        @JsonView(Views.Short::class)
        val currency: String = CURRENCY,

        @JsonIgnore
        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @MapsId
        val location: Location? = null,

        @JsonView(Views.Short::class)
        var distanceInKm: Double = 0.0,

        @JsonView(Views.Short::class)
        val validUntil: Long = 0
)