package com.deenislamic.sdk.service.network.response.islamicevent


import androidx.annotation.Keep

@Keep
internal data class IslamicEventListResponse(
    var data: List<Data>,
    var message: String? = null,
    var success: Boolean? = null,
    var totalData: Int? = null
) {
    @Keep
    data class Data(
        var category: String? = null,
        var id: Int,
        var imageurl: String? = null
    )
}
