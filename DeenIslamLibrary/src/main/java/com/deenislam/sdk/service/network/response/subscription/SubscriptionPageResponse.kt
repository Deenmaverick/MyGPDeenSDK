package com.deenislam.sdk.service.network.response.subscription

import androidx.annotation.Keep

@Keep
internal data class SubscriptionPageResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean
)