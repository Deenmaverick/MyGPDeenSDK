package com.deenislamic.sdk.service.callback.islamicname

import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameCategoriesResponse

internal interface IslamicNameCatCallback {
    fun onCatItemClick(data: IslamicNameCategoriesResponse.Data)
}