package com.deenislam.sdk.service.callback.common

import com.deenislam.sdk.service.network.response.common.CommonCardData

interface CommonCardCallback {

    fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int)
    {

    }
    fun singleCardItemClicked()
    {

    }
}