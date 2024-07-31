package com.deenislamic.sdk.service.network.response.gphome

import androidx.annotation.Keep

@Keep
internal data class Menu(
    val ArabicText: String,
    val Id: Int,
    val IsVisited: Boolean,
    val Text: String,
    val imageurl1: String,
    val isPremium: Boolean
)