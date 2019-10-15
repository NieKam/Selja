package io.selja.utils

import com.squareup.moshi.Moshi
import io.selja.model.Error
import retrofit2.HttpException

object ErrorMapper {
    private val jsonAdapter = Moshi.Builder().build().adapter<Error>(Error::class.java)

    @JvmStatic
    fun map(ex: Throwable): Error? {
        if (ex is HttpException) {
            val errorResponse = ex.response()?.errorBody()?.string()
            errorResponse ?: return null

            return jsonAdapter.fromJson(errorResponse)
        } else {
            return null
        }
    }
}