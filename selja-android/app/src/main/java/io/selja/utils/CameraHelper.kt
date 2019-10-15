package io.selja.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import io.selja.BuildConfig
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CODE = 100
const val FOLDER_NAME = "ads_images"

private const val NAME_FORMAT = "yyyyMMdd_HHmmss"

class CameraHelper {

    fun getPhotoFile(context: Context): File {
        return createFile(context)
    }

    fun getCameraIntent(context: Context, file: File): Intent {
        val photoURI = getUriFromFile(context, BuildConfig.APPLICATION_ID, file)

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
    }

    fun getPhotoFilePath(file: File) = "file://${file.absoluteFile}"

    private fun createFile(context: Context): File {
        val timeStamp = SimpleDateFormat(NAME_FORMAT, Locale.US).format(Date())
        val storageDir = getPicturesDir(context)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val image = File(storageDir, "${timeStamp}.jpg")
        if (!(image.createNewFile() || image.exists())) {
            throw FileNotFoundException(image.absolutePath)
        }
        return image
    }

    private fun getPicturesDir(context: Context): File {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }

        return File(dir, FOLDER_NAME)
    }

    private fun getUriFromFile(context: Context, appId: String, photoFile: File): Uri {
        return FileProvider.getUriForFile(
            context, "${appId}.provider", photoFile
        )
    }
}