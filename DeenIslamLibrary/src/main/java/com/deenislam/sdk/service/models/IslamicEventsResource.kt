package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.islamicevent.IslamicEventListResponse.Data
import com.deenislam.sdk.service.network.response.islamicevent.IslamicEventSubCatListResponse


internal interface IslamicEventsResource {
    data class islamicEvents(val data: List<Data>) : IslamicEventsResource
    data class SubCatList(val data: List<IslamicEventSubCatListResponse.Data>) :IslamicEventsResource
}