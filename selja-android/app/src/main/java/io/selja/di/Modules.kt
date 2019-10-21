package io.selja.di

import io.selja.BuildConfig
import io.selja.api.SeljaApi
import io.selja.base.DeviceId
import io.selja.permissions.PermissionManager
import io.selja.repository.AdItemsDataModel
import io.selja.repository.AdItemsDataSource
import io.selja.repository.LocationRepository
import io.selja.ui.details.AdItemDetailsViewModel
import io.selja.ui.newITem.NewItemViewModel
import io.selja.ui.overview.ItemsOverviewViewModel
import io.selja.ui.overview.adapter.ItemsAdapter
import io.selja.utils.CameraHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val mainModule = module {
    factory<AdItemsDataModel> { AdItemsDataSource(get()) }
    factory { PermissionManager() }
    factory { ItemsAdapter(get()) }
    factory { LocationRepository(get()) }
    factory { DeviceId() }
    viewModel { ItemsOverviewViewModel(get(), get()) }
}

val detailsModule = module {
    viewModel { AdItemDetailsViewModel(get(), get(), get()) }
}

val newItemModule = module {
    factory { CameraHelper() }
    viewModel { NewItemViewModel(get(), get(), get()) }
}

val networkModule = module {
    single { provideDefaultOkhttpClient() }
    single { provideRetrofit(get()) }
    single { provideSeljaService(get()) }
}

fun provideDefaultOkhttpClient(): OkHttpClient {
    val httpClientBuilder = OkHttpClient.Builder()
    addDebugInterceptor(httpClientBuilder)
    return httpClientBuilder.build()
}

fun provideSeljaService(retrofit: Retrofit): SeljaApi = retrofit.create(SeljaApi::class.java)

fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}

private fun addDebugInterceptor(httpClientBuilder: OkHttpClient.Builder) {
    if (BuildConfig.DEBUG) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClientBuilder.addInterceptor(interceptor)
    }
}


