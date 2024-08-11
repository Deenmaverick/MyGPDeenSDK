package com.deenislamic.sdk.service.network.response.islamicevent


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class IslamicEventListResponse(
    @SerializedName("Data")
    var data: List<Data>,
    var Message: String? = null,
    var Success: Boolean? = null,
    var TotalData: Int? = null
) {
    @Keep
    data class Data(
        var Id: Int,
        var category: String? = null,
        var imageurl: String? = null
    )
}

