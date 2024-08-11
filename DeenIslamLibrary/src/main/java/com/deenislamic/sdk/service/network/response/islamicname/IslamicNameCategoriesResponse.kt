package com.deenislamic.sdk.service.network.response.islamicname


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class IslamicNameCategoriesResponse(
    @SerializedName("Data")
    var data: List<Data>,
    var message: String? = null,
    var success: Boolean? = null,
    var totalData: Int? = null
) {
    @Keep
    data class Data(
        var gender: String? = null,
        var Id: Int,
        var Title: String? = null
    )
}
