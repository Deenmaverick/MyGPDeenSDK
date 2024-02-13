package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.islamifazael.Data
import com.deenislam.sdk.service.network.response.islamifazael.bycat.FazaelDataItem

internal interface IslamiFazaelCallback {

    fun FazaelCatClicked(getdata: Data){

    }
    fun FazaelShareClicked(getdata: FazaelDataItem){

    }
}