package com.deenislamic.sdk.service.callback.common

import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data

internal interface BasicCardListCallback {
    fun basicCardListItemSelect(data: Data)
    {

    }
    fun basicCardListItemSelect(data: Data,pos:Int)
    {

    }
}