package com.deenislamic.sdk.service.callback.islamicevent

import com.deenislamic.sdk.service.network.response.islamicevent.IslamicEventSubCatListResponse.Data

internal interface IslamicEventSubCatCallback {
    fun eventSubCatItemClick(item: Data)
}