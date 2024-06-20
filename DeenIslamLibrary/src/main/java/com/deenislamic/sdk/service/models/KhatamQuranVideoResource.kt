package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.common.CommonCardData


internal interface KhatamQuranVideoResource {
    data class khatamequranVideo(val data: List<CommonCardData>) : KhatamQuranVideoResource
    data class khatamequranRecentVideos(val data: List<CommonCardData>) : KhatamQuranVideoResource
}