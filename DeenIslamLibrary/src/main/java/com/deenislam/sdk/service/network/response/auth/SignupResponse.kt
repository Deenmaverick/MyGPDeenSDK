package com.deenislam.sdk.service.network.response.auth

internal data class SignupResponse(
    val Data: Any,
    val Message: String,
    val Success: Boolean,
    val error: String?
)