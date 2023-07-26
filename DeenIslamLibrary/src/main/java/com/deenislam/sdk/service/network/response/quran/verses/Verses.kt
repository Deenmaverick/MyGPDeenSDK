package com.deenislam.service.network.response.quran.verses

import com.deenislam.sdk.service.network.response.quran.verses.Pagination

data class Verses(
    val pagination: Pagination,
    val verses: List<Verse>
)