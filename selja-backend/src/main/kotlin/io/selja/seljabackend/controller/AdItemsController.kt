package io.selja.seljabackend.controller

import com.fasterxml.jackson.annotation.JsonView
import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.model.Location
import io.selja.seljabackend.model.NewAdItem
import io.selja.seljabackend.service.AdsService
import io.selja.seljabackend.service.StorageService
import io.selja.seljabackend.views.Views
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Min

private const val ADS_PATH = "/items"
private const val LAT_PARAM = "lat"
private const val LONG_PARAM = "long"

@RestController
@Validated
@CrossOrigin(origins = ["http://localhost:3000"])
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

    @PostMapping(ADS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    fun new(@Validated @RequestBody newItem: NewAdItem): AdItem {
        return adsService.createNewAd(newItem)
    }

    @PutMapping("${ADS_PATH}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadPhoto(@RequestParam("file") file: MultipartFile, @PathVariable @Min(1) id: Long): AdItem {
        val url = storageService.store(file)
        return adsService.addPhotoToItem(id, url)
    }
}