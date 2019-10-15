package io.selja.repository

import android.location.Location
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

    override suspend fun createNew(newAdItem: NewAdItem): AdItem {
        return seljaApi.createNewAd(newAdItem)
    }

    override suspend fun uploadFile(id: Long, path: String): AdItem {
        val photoFile = File(path)
        val fileReqBody = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", photoFile.name, fileReqBody)
        return seljaApi.uploadPhoto(part, id)
    }
}