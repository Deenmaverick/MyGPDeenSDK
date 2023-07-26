package com.deenislam.sdk.service.network.response.quran.verses

data class Audio(
    val segments: List<List<Int>>,
    val url: String
)