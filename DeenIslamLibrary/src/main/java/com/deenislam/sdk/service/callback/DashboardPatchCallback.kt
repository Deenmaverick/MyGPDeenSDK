package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.dashboard.Item

internal interface DashboardPatchCallback {
    fun dashboardPatchClickd(patch: String, data: Item?=null)
}