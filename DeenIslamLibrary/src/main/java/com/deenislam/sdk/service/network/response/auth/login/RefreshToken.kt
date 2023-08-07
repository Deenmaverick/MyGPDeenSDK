package com.deenislam.sdk.service.network.response.auth.login

import androidx.annotation.Keep

@Keep
internal data class RefreshToken(
    val Created: String,
    val Expires: String,
    val Token: String
)