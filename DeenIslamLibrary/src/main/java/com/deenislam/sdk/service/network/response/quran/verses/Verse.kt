package com.deenislam.service.network.response.quran.verses

import com.deenislam.sdk.service.network.response.quran.verses.Audio

data class Verse(
    val hizb_number: Int,
    val id: Int,
    val juz_number: Int,
    val manzil_number: Int,
    val page_number: Int,
    val rub_el_hizb_number: Int,
    val ruku_number: Int,
    val sajdah_number: Any,
    val verse_key: String,
    val verse_number: Int,
    val words: List<Word>,
    val translations : List<Translations>,
    val audio : Audio
)