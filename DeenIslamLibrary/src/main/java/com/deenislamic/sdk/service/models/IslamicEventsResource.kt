package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.islamicevent.IslamicEventListResponse.Data
import com.deenislamic.sdk.service.network.response.islamicevent.IslamicEventSubCatListResponse


internal interface IslamicEventsResource {
    data class islamicEvents(val data: List<Data>) : IslamicEventsResource
    data class SubCatList(val data: List<IslamicEventSubCatListResponse.Data>) :IslamicEventsResource
}