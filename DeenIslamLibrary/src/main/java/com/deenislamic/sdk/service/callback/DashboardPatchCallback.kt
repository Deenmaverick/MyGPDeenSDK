package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.dashboard.Item

internal interface DashboardPatchCallback {
    fun dashboardPatchClickd(patch: String, data: Item?=null)
}