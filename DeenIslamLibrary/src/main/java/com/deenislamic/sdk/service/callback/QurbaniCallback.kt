package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.service.network.response.dashboard.Item

internal interface QurbaniCallback {
    fun selectedQurbaniBanner(getdata: Data) {

    }
    fun selectedQurbaniCat(menuData: Item){

    }

    fun qurbaniHaatDirection(getdata: Data) {

    }

    fun qurbaniCommonContentClicked(absoluteAdapterPosition: Int, isExpanded: Boolean) {

    }

    fun menu3dotClicked(getdata: Data) {

    }

}