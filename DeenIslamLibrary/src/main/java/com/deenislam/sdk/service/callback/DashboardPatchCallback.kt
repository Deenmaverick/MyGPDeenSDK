package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.dashboard.Banner

internal interface DashboardPatchCallback {
    fun dashboardPatchClickd(patch: String, banner: Banner?=null)
}