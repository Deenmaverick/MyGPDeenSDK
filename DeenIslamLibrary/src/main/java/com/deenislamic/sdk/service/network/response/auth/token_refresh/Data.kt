package com.deenislamic.sdk.service.network.response.auth.token_refresh

import androidx.annotation.Keep

@Keep
internal data class Data(
    val JWT: String,
    val RefreshToken: RefreshToken
)