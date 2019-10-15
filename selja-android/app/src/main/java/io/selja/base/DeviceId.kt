package io.selja.base

import android.provider.Settings


class DeviceId {
    fun getDeviceId() = Settings.Secure.ANDROID_ID
}