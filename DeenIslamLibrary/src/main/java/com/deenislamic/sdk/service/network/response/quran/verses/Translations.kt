package com.deenislamic.sdk.service.network.response.quran.verses

import androidx.annotation.Keep

@Keep
internal data class Translations(
    val id: Int,
    val resource_id: Int,
    val text:String
)
