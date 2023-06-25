package com.deenislam.sdk.service.network.response.auth.login

data class Data(
    val JWT: String,
    val RefreshToken: RefreshToken
)