package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.islamiceducationvideo.Data


internal interface IslamicEducationVideoResource {
    data class educationVideo(val data: Data) : IslamicEducationVideoResource
    data class addHistoryDone(val bol:Boolean): IslamicEducationVideoResource
}