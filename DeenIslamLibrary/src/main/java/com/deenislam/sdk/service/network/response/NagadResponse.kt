package com.deenislam.sdk.service.network.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
internal data class NagadResponse(
    @SerializedName("data")
    var `data`: String,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("success")
    var success: Boolean
)