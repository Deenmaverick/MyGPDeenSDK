package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.advertisement.Data

internal interface AdvertisementCallback {

    fun adClicked(items: Data) {

    }
}