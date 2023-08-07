package com.deenislam.sdk.service.network.response.prayertimes

import androidx.annotation.Keep

@Keep
internal data class WaktTracker(
    var Wakt: String,
    var status: Boolean
)