package com.deenislamic.sdk.service.network.response.quran.verses

import androidx.annotation.Keep

@Keep
internal data class Audio(
    val segments: List<List<Int>>,
    val url: String
)