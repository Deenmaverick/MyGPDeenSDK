package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.libs.calendar.CalendarDay
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.dashboard.Item

internal interface RamadanCallback {
    fun openMonthlyTracker()
    {

    }
    fun setFastingTrack(isFast: Boolean)
    {

    }

    fun stateSelected(stateModel: StateModel){}

    fun patchClicked(data: Item) {

    }

    fun sehriCardClicked(sehriTime: String) {

    }

    fun iftarCardClicked(iftaar: String) {

    }

    fun selectedCalendar(day: CalendarDay) {

    }
}