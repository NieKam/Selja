package io.selja.ui.overview

import io.selja.model.AdItem
import io.selja.model.imageUrlWithHost

data class ItemDetailsViewModel(val adItem: AdItem, val deviceId: String) {
    fun getImageUrl() = adItem.imageUrlWithHost()
    fun isMine() = adItem.deviceId == deviceId
}