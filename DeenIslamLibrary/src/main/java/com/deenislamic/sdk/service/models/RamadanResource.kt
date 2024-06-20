package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.ramadan.Data

internal interface RamadanResource {

    data class ramadanTime(val data: Data) :RamadanResource
    data class ramadanTracking(val isFasting: Boolean) :RamadanResource
    data class ramadanCalendar(val data: com.deenislamic.sdk.service.network.response.ramadan.calendar.Data) :RamadanResource
    data class RamadanPatch(
        val data: Data?,
        val patch: List<com.deenislamic.sdk.service.network.response.dashboard.Data>?
    ) :RamadanResource
}