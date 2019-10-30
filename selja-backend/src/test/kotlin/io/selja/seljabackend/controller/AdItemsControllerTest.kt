package io.selja.seljabackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.model.NewAdItem
import io.selja.seljabackend.model.toAdItem
import io.selja.seljabackend.service.AdsService
import io.selja.seljabackend.service.StorageService
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@RunWith(SpringRunner::class)
@WebMvcTest
class AdItemsControllerTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var adsService: AdsService

    @MockBean
    private lateinit var storageService: StorageService

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun testGetAll_descriptionIsNotIncluded() {
        val name = "Test"
        val desc = "Description"
        val ad1 = AdItem(id = 1, name = name, description = desc)
        val ad2 = AdItem(id = 2, name = name, description = desc)
        whenever(adsService.getAll(null)).thenReturn(listOf(ad1, ad2))

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("\$.[0].name").value(ad1.name))
                .andExpect(jsonPath("\$.[1].name").value(ad2.name))
                .andExpect(jsonPath("\$.[0].description").doesNotExist())
                .andExpect(jsonPath("\$.[1].description").doesNotExist())
    }

    @Test
    fun testGetOne_descriptionIsIncluded() {
        val name = "Test"
        val desc = "Description"
        val ad = AdItem(id = 1, name = name, description = desc)
        whenever(adsService.getOne(1, null)).thenReturn(ad)

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("\$.name").value(ad.name))
                .andExpect(jsonPath("\$.description").value(desc))
    }

    @Test
    fun testStoreNewAdWithPhoto() {
        val imageUrl = "photo-url"
        val newItem = NewAdItem(deviceId = "dev-Id", name = "name", description = "desc", phone = "123456", price = 10.0, duration = 60_000, lat = 50.0, long = 10.0)
        val adItem = newItem.toAdItem().apply {
            photoUrl = imageUrl
        }
        val mockImage = MockMultipartFile("photo", "User Photo".toByteArray())
        val mockJson = MockMultipartFile("ad", "", "application/json", mapper.writeValueAsString(newItem).toByteArray())


        whenever(adsService.saveNewAd(any())).thenReturn(adItem)
        whenever(storageService.store(any())).thenReturn(imageUrl)
        mockMvc.perform(MockMvcRequestBuilders.multipart("/items")
                .file(mockImage)
                .file(mockJson))
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("\$.name").value(newItem.name))
                .andExpect(jsonPath("\$.description").value(newItem.description))
                .andExpect(jsonPath("\$.photoUrl").value(imageUrl))
                .andExpect(jsonPath("\$.phoneObfuscated").exists())
                .andExpect(jsonPath("\$.validUntilMs", Matchers.greaterThan(System.currentTimeMillis())))
                .andExpect(jsonPath("\$.id").exists())
    }

    @Test
    fun testStoreNewAdNoPhoto() {
        val newItem = NewAdItem(deviceId = "dev-Id", name = "name", description = "desc", phone = "12345", price = 10.0, duration = 60_000, lat = 50.0, long = 10.0)
        val adItem = newItem.toAdItem()
        val mockJson = MockMultipartFile("ad", "", "application/json", mapper.writeValueAsString(newItem).toByteArray())
        whenever(adsService.saveNewAd(any())).thenReturn(adItem)

        mockMvc.perform(MockMvcRequestBuilders.multipart("/items")
                .file(mockJson))
                .andExpect(status().isCreated)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.photoUrl", Matchers.emptyString()))
    }
}