package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.eidjamat.EidJamatListResponse

internal interface EidJamatCallback {
    fun mosqueDirectionClicked(data: EidJamatListResponse.Data){

    }
    fun mosqueShareClicked(getData: EidJamatListResponse.Data){

    }
}