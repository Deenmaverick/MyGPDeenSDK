package com.deenislamic.sdk.service.network.response.quran.qurangm.ayat

import androidx.annotation.Keep

@Keep
internal data class Ayath(
    val Arabic_indopak: String,
    val Arabic_uthmani: String,
    val Arabic_Custom:String,
    val AudioUrl: String,
    val Id: Int,
    var IsFavorite: Boolean,
    val JuzId: Int,
    val Serial: Int,
    val SurahId: Int,
    val SurahName: String,
    val Translations: List<Translation>,
    val Transliteration_bn: String,
    val Transliteration_en: String,
    val VerseId: Int,
    val VerseKey: String
)