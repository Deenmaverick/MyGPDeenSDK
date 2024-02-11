package com.deenislam.sdk.service.network.response.quran.learning.quransikkhaacademy

import androidx.annotation.Keep

@Keep
internal data class CreateUserResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val status: String
) {

    @Keep
    internal data class Data(
        val order_id: String
    )
}

