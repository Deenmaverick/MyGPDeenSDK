package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.network.response.ramadan.Data

internal interface RamadanResource {

    data class ramadanTime(val data: Data) :RamadanResource
    data class ramadanTracking(val isFasting: Boolean) :RamadanResource
    data class ramadanCalendar(val data: com.deenislam.sdk.service.network.response.ramadan.calendar.Data) :RamadanResource
    data class RamadanPatch(
        val data: Data?,
        val patch: List<com.deenislam.sdk.service.network.response.dashboard.Data>?
    ) :RamadanResource
}