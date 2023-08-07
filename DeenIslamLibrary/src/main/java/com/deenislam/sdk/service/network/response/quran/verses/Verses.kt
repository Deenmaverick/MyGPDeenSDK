package com.deenislam.sdk.service.network.response.quran.verses

import androidx.annotation.Keep

@Keep
internal data class Verses(
    val pagination: Pagination,
    val verses: List<Verse>
)