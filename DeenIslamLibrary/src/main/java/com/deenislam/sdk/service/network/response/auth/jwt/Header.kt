package com.deenislam.sdk.service.network.response.auth.jwt

internal data class Header(
    val alg: String,
    val typ: String
)