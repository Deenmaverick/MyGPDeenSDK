package com.deenislam.sdk.service.network.response.auth.login

import androidx.annotation.Keep

@Keep
internal data class Data(
    val JWT: String,
    val RefreshToken: RefreshToken
)