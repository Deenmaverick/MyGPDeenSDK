package com.deenislamic.sdk.service.network.response.share

import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.share.Data

@Keep
internal data class WallpaperListResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)