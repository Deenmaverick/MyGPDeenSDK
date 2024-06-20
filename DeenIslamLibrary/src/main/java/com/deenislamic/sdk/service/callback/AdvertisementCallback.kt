package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.advertisement.Data

internal interface AdvertisementCallback {

    fun adClicked(items: Data) {

    }
}