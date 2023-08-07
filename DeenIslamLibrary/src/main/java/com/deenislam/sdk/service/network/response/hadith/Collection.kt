package com.deenislam.sdk.service.network.response.hadith

import androidx.annotation.Keep

@Keep
internal data class Collection(
    val lang: String,
    val shortIntro: String,
    val title: String
)