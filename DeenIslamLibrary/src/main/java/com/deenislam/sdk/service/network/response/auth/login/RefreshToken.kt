package com.deenislam.sdk.service.network.response.auth.login

internal data class RefreshToken(
    val Created: String,
    val Expires: String,
    val Token: String
)