package com.deenislam.sdk.service.network.response.auth.jwt

internal data class Payload(
    val Application: String,
    val exp: Int,
    val iat: Int,
    val name: String,
    val nbf: Int,
    val role: String
)