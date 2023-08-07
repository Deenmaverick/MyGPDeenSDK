package com.deenislam.sdk.service.network.response.quran.verses

import androidx.annotation.Keep

@Keep
internal data class Transliteration(
    val language_name: String,
    val text: String
)