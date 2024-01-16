package com.deenislam.sdk.service.callback.islamicevent

import com.deenislam.sdk.service.network.response.islamicevent.IslamicEventListResponse

internal interface IslamicEventCallback {
    fun eventCatItemClick(item: IslamicEventListResponse.Data)
}