package io.selja.seljabackend.service

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.multipart.MultipartFile

@RunWith(SpringRunner::class)
class StorageServiceImplTest {
    @TestConfiguration
    internal class StorageServiceImplTestContextConfiguration {
        @Bean
        fun storageService(): StorageService {
            return StorageServiceImpl()
        }
    }

    @Autowired
    private lateinit var storageService: StorageService

    @Test
    fun testStoreFile() {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.bytes).thenReturn("".toByteArray())

        val url = storageService.store(mockFile)

        assertThat(url).isNotEmpty()
        assertThat(url).startsWith(UPLOADED_FOLDER)

        storageService.delete(url)
    }
}