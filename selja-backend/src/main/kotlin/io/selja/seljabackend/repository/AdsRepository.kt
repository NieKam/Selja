package io.selja.seljabackend.repository

import io.selja.seljabackend.model.AdItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AdsRepository : JpaRepository<AdItem, Long> {

    @Query("SELECT a FROM AdItem a WHERE a.validUntilMs <= :timestamp")
    fun findAllOlderThan(timestamp: Long): List<AdItem>

    @Query("SELECT a, " +
            "ACOS( SIN( RADIANS( a.location.lat ) ) * SIN( RADIANS( :lat ) ) + COS( RADIANS( a.location.lat ) ) * " +
            "COS( RADIANS( :lat )) * COS( RADIANS( a.location.long ) - RADIANS( :long )) ) * 6380 AS distance " +
            "FROM AdItem a WHERE " +
            "ACOS( SIN( RADIANS( a.location.lat ) ) * SIN( RADIANS( :lat ) ) + COS( RADIANS( a.location.lat ) ) * " +
            "COS( RADIANS( :lat )) * COS( RADIANS( a.location.long ) - RADIANS( :long )) ) * 6380 < :radiusKm " +
            "ORDER BY distance")
    fun findAllInArea(lat: Double, long: Double, radiusKm: Double): List<AdItem>
}