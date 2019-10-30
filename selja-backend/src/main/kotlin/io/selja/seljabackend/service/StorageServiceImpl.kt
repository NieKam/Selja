package io.selja.seljabackend.service

import net.bytebuddy.utility.RandomString
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

const val UPLOADED_FOLDER = "images/"

@Service
class StorageServiceImpl : StorageService {

    override fun delete(url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        Files.delete(Paths.get(url))
    }

    override fun store(file: MultipartFile): String {
        val newName = UPLOADED_FOLDER + generateName()
        val bytes = file.bytes
        val path = Paths.get(newName)

        Files.write(path, bytes).toAbsolutePath().toString()
        return newName
    }

    private fun generateName() = RandomString.make(10)
}