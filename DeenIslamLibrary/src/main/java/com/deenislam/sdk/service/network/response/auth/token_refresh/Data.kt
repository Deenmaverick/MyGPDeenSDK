package com.deenislam.sdk.service.network.response.auth.token_refresh

data class Data(
    val JWT: String,
    val RefreshToken: RefreshToken
)