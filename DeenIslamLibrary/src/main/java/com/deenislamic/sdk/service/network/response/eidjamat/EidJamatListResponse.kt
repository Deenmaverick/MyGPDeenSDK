package com.deenislamic.sdk.service.network.response.eidjamat


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
internal data class EidJamatListResponse(
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
    internal data class Data(
        @SerializedName("Address")
        var address: String? = null,
        @SerializedName("Division")
        var division: Any? = null,
        @SerializedName("Id")
        var id: Int? = null,
        @SerializedName("Latitude")
        var latitude: String? = null,
        @SerializedName("Longitude")
        var longitude: String? = null,
        @SerializedName("Text")
        var text: String? = null,
        @SerializedName("Title")
        var title: String? = null
    )
}