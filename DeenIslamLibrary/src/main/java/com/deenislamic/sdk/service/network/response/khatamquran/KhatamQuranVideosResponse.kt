package com.deenislamic.sdk.service.network.response.khatamquran


import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.network.response.paging.Pagination

@Keep
internal data class KhatamQuranVideosResponse(
    var data: List<CommonCardData>?,
    var message: String? = null,
    var pagination: Pagination? = null,
    var success: Boolean? = null
) {
    internal data class Data(
        var category: String? = null,
        var categoryID: Int? = null,
        var duration: Any? = null,
        var id: Int? = null,
        var imageurl: String? = null,
        var isFavorite: Boolean? = null,
        var reference: Any? = null,
        var referenceurl: Any? = null,
        var title: String? = null,
        var videourl: String? = null
    )

}
