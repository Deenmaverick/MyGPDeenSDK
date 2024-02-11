package com.deenislam.sdk.service.network.response.quran.learning.quransikkhaacademy

import androidx.annotation.Keep

@Keep
internal data class GetUserResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val status: String
)
{
    @Keep
    internal data class Data(
        val email: Any,
        val name: String,
        val phone: String,
        val user_token: UserToken
    )

    @Keep
    internal data class UserToken(
        val accesstoken: String,
        val accesstoken_exp: String
    )

}

