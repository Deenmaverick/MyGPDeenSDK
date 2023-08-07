package com.deenislam.sdk.service.network.response.auth.login

import androidx.annotation.Keep

@Keep
internal data class LoginResponse(
    val Data: Data?,
    val Message: String,
    val Success: Boolean
)