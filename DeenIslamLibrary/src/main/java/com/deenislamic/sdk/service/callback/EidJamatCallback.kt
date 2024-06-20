package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.eidjamat.EidJamatListResponse

internal interface EidJamatCallback {
    fun mosqueDirectionClicked(data: EidJamatListResponse.Data){

    }
    fun mosqueShareClicked(getData: EidJamatListResponse.Data){

    }
}