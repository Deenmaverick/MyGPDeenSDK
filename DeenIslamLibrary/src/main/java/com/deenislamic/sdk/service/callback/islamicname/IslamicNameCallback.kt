package com.deenislamic.sdk.service.callback.islamicname

import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameHomeResponse

internal interface IslamicNameCallback {
    fun onItemClick(data: IslamicNameHomeResponse.Data.Item)
}