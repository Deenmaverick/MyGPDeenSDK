package com.deenislam.sdk.service.network.response.quran.verses

import androidx.annotation.Keep

@Keep
internal data class Word(
    val audio_url: String,
    val char_type_name: String,
    val code_v1: String,
    val id: Int,
    val line_number: Int,
    val page_number: Int,
    val position: Int,
    val text: String,
    val translation: Translation,
    val transliteration: Transliteration
)