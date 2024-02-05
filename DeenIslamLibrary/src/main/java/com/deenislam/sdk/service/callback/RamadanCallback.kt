package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.models.ramadan.StateModel
import com.deenislam.sdk.service.network.response.dashboard.Item

internal interface RamadanCallback {
    fun openMonthlyTracker()
    {

    }
    fun setFastingTrack(isFast: Boolean)
    {

    }

    fun stateSelected(stateModel: StateModel)

    fun patchClicked(data: Item) {

    }
}