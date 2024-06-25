package com.deenislamic.sdk.service.network.response.share

import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Head

@Keep
internal data class WallpaperCat(
val Data: List<Head>,
val Message: String,
val Success: Boolean,
val TotalData: Int
)
