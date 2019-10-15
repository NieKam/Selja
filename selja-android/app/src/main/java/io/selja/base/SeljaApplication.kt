package io.selja.base

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import io.selja.di.detailsModule
import io.selja.di.mainModule
import io.selja.di.networkModule
import io.selja.di.newItemModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class SeljaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        startKoin {
            androidLogger()
            androidContext(this@SeljaApplication)
            modules(listOf(mainModule, detailsModule, networkModule, newItemModule))
        }
    }
}