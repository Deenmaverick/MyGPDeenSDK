package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.islamiccalendar.IslamicEvent

internal interface IslamicCalEventCallback {
    fun itemPosition(eventName: String, eventDate: String)
    fun itemClick(event: IslamicEvent)
}