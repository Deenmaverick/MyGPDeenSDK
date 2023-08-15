package com.deenislam.sdk.service.network.response.auth.jwt

import androidx.annotation.Keep

@Keep
internal data class Header(
    val alg: String,
    val typ: String
)