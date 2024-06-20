package com.deenislamic.sdk.service.callback.common

import com.deenislamic.sdk.service.network.response.dashboard.Item

internal interface HorizontalCardListCallback {

    fun patchItemClicked(getData: Item)

    fun smallCardPatchItemClicked(getData: Item){

    }
}