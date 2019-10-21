package io.selja.seljabackend.controller

import com.fasterxml.jackson.annotation.JsonView
import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.model.Location
import io.selja.seljabackend.model.NewAdItem
import io.selja.seljabackend.model.toAdItem
import io.selja.seljabackend.service.AdsService
import io.selja.seljabackend.service.StorageService
import io.selja.seljabackend.views.Views
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Min

private const val ADS_PATH = "/items"
private const val LAT_PARAM = "lat"
private const val LONG_PARAM = "long"

@RestController
@Validated
class AdItemsController {

    @Autowired
    lateinit var adsService: AdsService

    @Autowired
    lateinit var storageService: StorageService

    @GetMapping(ADS_PATH)
    @JsonView(Views.Short::class)
    fun all(@RequestParam(value = LAT_PARAM, defaultValue = "") lat: String,
            @RequestParam(value = LONG_PARAM, defaultValue = "") long: String): List<AdItem> {

        return adsService.getAll(Location.create(lat, long))
    }

    @GetMapping("${ADS_PATH}/{id}")
    @JsonView(Views.Full::class)
    fun one(@PathVariable @Min(1) id: Long,
            @RequestParam(value = LAT_PARAM, defaultValue = "") lat: String,
            @RequestParam(value = LONG_PARAM, defaultValue = "") long: String): AdItem {
        return adsService.getOne(id, Location.create(lat, long))
    }

    @PostMapping(value = [ADS_PATH], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun new(@Validated @RequestPart(value = "ad") newItem: NewAdItem,
            @RequestPart(value = "photo", required = false) photo: MultipartFile?): AdItem {
        val adItem = newItem.toAdItem()
        photo?.let {
            adItem.photoUrl = storageService.store(it)
        }

        return adsService.saveNewAd(adItem)
    }
}