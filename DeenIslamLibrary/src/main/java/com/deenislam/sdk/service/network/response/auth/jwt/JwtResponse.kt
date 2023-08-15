package com.deenislam.sdk.service.network.response.auth.jwt

import androidx.annotation.Keep

@Keep
internal data class JwtResponse(
    val header: Header,
    val payload: Payload
)