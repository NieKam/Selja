package io.selja.repository

import android.location.Location
import io.selja.api.MULTIPART_IMAGE_NAME
import io.selja.api.SeljaApi
import io.selja.model.AdItem
import io.selja.model.NewAdItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AdItemsDataSource(private val seljaApi: SeljaApi) : AdItemsDataModel {
    override suspend fun getAll(location: Location?): List<AdItem> {
        return if (location == null) {
            seljaApi.getAllItems()
        } else {
            seljaApi.getAllItemsWithLocation(location.latitude.toString(), location.longitude.toString())
        }
    }

    override suspend fun getOne(id: Long, location: Location?): AdItem {
        return if (location == null) {
            seljaApi.getOne(id)
        } else {
            seljaApi.getOneWithLocation(id, location.latitude.toString(), location.longitude.toString())
        }
    }

    override suspend fun createNew(newAdItem: NewAdItem, path: String?): AdItem {
        val part: MultipartBody.Part? = path?.let {
            val photoFile = File(it)
            val fileReqBody = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(MULTIPART_IMAGE_NAME, photoFile.name, fileReqBody)
        }

        return seljaApi.createNewAd(newAdItem, part)
    }
}