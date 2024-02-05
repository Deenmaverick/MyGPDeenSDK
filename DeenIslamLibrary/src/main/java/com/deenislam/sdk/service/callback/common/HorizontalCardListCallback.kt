package com.deenislam.sdk.service.callback.common

import com.deenislam.sdk.service.network.response.dashboard.Item

internal interface HorizontalCardListCallback {

    fun patchItemClicked(getData: Item)

    fun smallCardPatchItemClicked(getData: Item){

    }
}