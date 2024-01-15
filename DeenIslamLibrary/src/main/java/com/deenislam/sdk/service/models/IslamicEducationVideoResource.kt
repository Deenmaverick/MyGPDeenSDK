package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.islamiceducationvideo.Data


interface IslamicEducationVideoResource {
    data class educationVideo(val data: Data) : IslamicEducationVideoResource
    data class addHistoryDone(val bol:Boolean): IslamicEducationVideoResource
}