package com.deenislam.sdk.service.network.response.auth.jwt

import androidx.annotation.Keep

@Keep
internal data class Payload(
    val Application: String,
    val exp: Int,
    val iat: Int,
    val name: String,
    val nbf: Int,
    val role: String
)