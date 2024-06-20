package com.deenislamic.sdk.service.network.body.youtube

import androidx.annotation.Keep

@Keep
internal data class Client(
    val clientName: String = "WEB",
    val clientVersion: String = "2.20210728.00.00",
    val gl: String = "GB",
    val hl: String = "en-GB"
)