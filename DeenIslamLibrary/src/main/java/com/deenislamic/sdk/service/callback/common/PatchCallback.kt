package com.deenislamic.sdk.service.callback.common

import com.deenislamic.sdk.service.network.response.dashboard.Item

internal interface PatchCallback {

    fun patchClicked(data: Item)
}