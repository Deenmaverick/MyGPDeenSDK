package com.deenislam.sdk.service.network.response.auth.token_refresh

import androidx.annotation.Keep

@Keep
internal data class RefreshToken(
    val Created: String,
    val Expires: String,
    val Token: String
)