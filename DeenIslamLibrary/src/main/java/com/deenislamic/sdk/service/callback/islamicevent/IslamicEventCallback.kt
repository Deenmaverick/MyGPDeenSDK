package com.deenislamic.sdk.service.callback.islamicevent

import com.deenislamic.sdk.service.network.response.islamicevent.IslamicEventListResponse

internal interface IslamicEventCallback {
    fun eventCatItemClick(item: IslamicEventListResponse.Data)
}