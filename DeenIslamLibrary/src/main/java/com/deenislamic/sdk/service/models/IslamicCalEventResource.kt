package com.deenislamic.sdk.service.models


import com.deenislamic.sdk.service.network.response.islamiccalendar.CalendarData
import com.deenislamic.sdk.service.network.response.islamiccalendar.IslamicEventDataDetail

internal interface IslamicCalEventResource {
    data class IslamicCalendarEvents(val data: List<IslamicEventDataDetail>): IslamicCalEventResource
    data class IslamicCalendar(val data: List<CalendarData>): IslamicCalEventResource
}