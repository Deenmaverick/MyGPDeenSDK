package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.islamifazael.Data
import com.deenislamic.sdk.service.network.response.islamifazael.bycat.FazaelDataItem

internal interface IslamiFazaelCallback {

    fun FazaelCatClicked(getdata: Data){

    }
    fun FazaelShareClicked(getdata: FazaelDataItem){

    }
}