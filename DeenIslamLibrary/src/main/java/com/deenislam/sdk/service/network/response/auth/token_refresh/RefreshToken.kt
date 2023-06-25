package com.deenislam.sdk.service.network.response.auth.token_refresh

internal data class RefreshToken(
    val Created: String,
    val Expires: String,
    val Token: String
)