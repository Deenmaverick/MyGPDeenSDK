package com.deenislamic.sdk.service.callback.common

import com.deenislamic.sdk.service.network.response.common.CommonCardData

internal interface CommonCardCallback {

    fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int)
    {

    }
    fun singleCardItemClicked()
    {

    }
}