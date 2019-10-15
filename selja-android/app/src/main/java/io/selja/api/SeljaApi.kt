package io.selja.api

import io.selja.model.AdItem
import io.selja.model.NewAdItem
import okhttp3.MultipartBody
import retrofit2.http.*

private const val PATH = "/items"
private const val PATH_ID = "$PATH/{id}"
private const val QUERY_PARAM_LAT = "lat"
private const val QUERY_PARAM_LONG = "long"

interface SeljaApi {
    @GET(PATH)
    suspend fun getAllItems(): List<AdItem>

    @GET(PATH)
    suspend fun getAllItemsWithLocation(@Query(QUERY_PARAM_LAT) lat: String, @Query(QUERY_PARAM_LONG) lon: String): List<AdItem>

    @GET(PATH_ID)
    suspend fun getOne(@Path("id") id: Long): AdItem

    @GET(PATH_ID)
    suspend fun getOneWithLocation(@Path("id") id: Long, @Query(QUERY_PARAM_LAT) lat: String, @Query(QUERY_PARAM_LONG) lon: String): AdItem

    @POST(PATH)
    suspend fun createNewAd(@Body requestBody: NewAdItem): AdItem

    @Multipart
    @PUT(PATH_ID)
    suspend fun uploadPhoto(@Part file: MultipartBody.Part, @Path("id") id: Long): AdItem
}