package io.selja.seljabackend.controller

import com.nhaarman.mockitokotlin2.whenever
import io.selja.seljabackend.model.AdItem
import io.selja.seljabackend.service.AdsService
import io.selja.seljabackend.service.StorageService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("\$.name").value(ad.name))
                .andExpect(jsonPath("\$.description").value(desc))
    }
}