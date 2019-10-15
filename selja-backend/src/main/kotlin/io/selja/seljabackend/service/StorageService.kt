package io.selja.seljabackend.service

import org.springframework.web.multipart.MultipartFile

interface StorageService {
    fun store(file: MultipartFile): String

    fun delete(url: String?)
}