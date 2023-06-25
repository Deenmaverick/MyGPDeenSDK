package com.deenislam.sdk.service.network.response.auth.login

internal data class Data(
    val JWT: String,
    val RefreshToken: RefreshToken
)