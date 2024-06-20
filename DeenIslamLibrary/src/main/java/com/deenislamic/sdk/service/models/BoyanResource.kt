package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.boyan.videopreview.BoyanVideoPreviewResponse
import com.deenislamic.sdk.service.network.response.dashboard.Data

internal interface BoyanResource {

    data class BoyanHomeData(val data: List<Data>) : BoyanResource

    data class BoyanCategoryData(val data: List<com.deenislamic.sdk.service.network.response.boyan.categoriespaging.Data>) : BoyanResource

    data class BoyanScholarData(val data: List<com.deenislamic.sdk.service.network.response.boyan.scholarspaging.Data>): BoyanResource

    data class BoyanVideoData(val data: BoyanVideoPreviewResponse): BoyanResource
}