package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislamic.sdk.service.network.response.share.Data

internal interface ShareResource {

    data class WallpaperCat(val data: List<Head>) :ShareResource
    data class Wallpaper(val data: List<Data>) :ShareResource
}