package com.deenislam.sdk.service.callback.islamicevent

import com.deenislam.sdk.service.network.response.islamicevent.IslamicEventSubCatListResponse.Data

internal interface IslamicEventSubCatCallback {
    fun eventSubCatItemClick(item: Data)
}