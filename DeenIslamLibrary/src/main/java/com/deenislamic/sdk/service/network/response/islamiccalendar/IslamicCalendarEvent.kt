package com.deenislamic.sdk.service.network.response.islamiccalendar

import androidx.annotation.Keep

@Keep
internal data class IslamicEventData(
    val Success: Boolean,
    val Message: String,
    val TotalData: Int,
    val Data: IslamicEventDataDetail
)

@Keep
internal data class IslamicEventDataDetail(
    val upcomingEvent: IslamicEvent,
    val Event: List<IslamicEvent>
)

@Keep
internal data class IslamicEvent(
    val Date: String,
    val text: String,
    val IslamicDate: String,
    val BanglaDate: String,
    val Status: String,
    val imageurl: String,
    val CategoryId: String,
    val ContentType:String,
    val isUpcoming:Boolean = false
)
