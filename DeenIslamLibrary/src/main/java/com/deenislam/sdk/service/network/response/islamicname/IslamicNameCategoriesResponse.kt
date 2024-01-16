package com.deenislam.sdk.service.network.response.islamicname


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
internal data class IslamicNameCategoriesResponse(
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
        @SerializedName("gender")
        var gender: String? = null,
        @SerializedName("Id")
        var id: Int,
        @SerializedName("Title")
        var title: String? = null
    )
}