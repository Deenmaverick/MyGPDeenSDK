package com.deenislam.sdk.service.network.response.auth.jwt

internal data class JwtResponse(
    val header: Header,
    val payload: Payload
)