package com.deenislamic.sdk.service.callback

import android.graphics.Bitmap
import com.deenislamic.sdk.service.network.response.dashboard.Item

internal interface DashboardPatchCallback {
    fun dashboardPatchClickd(patch: String, data: Item?=null)

    fun shareImage(bitmap: Bitmap) {

    }
}