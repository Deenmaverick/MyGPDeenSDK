package com.deenislamic.sdk.service.models


import com.deenislamic.sdk.service.network.response.islamiccalendar.IslamicEventDataDetail

internal interface IslamicCalEventResource {
    data class IslamicCalendarEvents(val data: List<IslamicEventDataDetail>): IslamicCalEventResource
}