package com.deenislamic.sdk.service.network.response.islamicevent


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class IslamicEventListResponse(
    @SerializedName("Data")
    var `data`: List<Data>,
    @SerializedName("Message")
    var message: String? = null,
    @SerializedName("Success")
    var success: Boolean? = null,
    @SerializedName("TotalData")
    var totalData: Int? = null
) {
    @Keep
    data class Data(
        @SerializedName("category")
        var category: String? = null,
        @SerializedName("Id")
        var id: Int,
        @SerializedName("imageurl")
        var imageurl: String? = null
    )
}