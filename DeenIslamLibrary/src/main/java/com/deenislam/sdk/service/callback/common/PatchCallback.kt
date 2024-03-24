package com.deenislam.sdk.service.callback.common

import com.deenislam.sdk.service.network.response.dashboard.Item

internal interface PatchCallback {

    fun patchClicked(data: Item)
}