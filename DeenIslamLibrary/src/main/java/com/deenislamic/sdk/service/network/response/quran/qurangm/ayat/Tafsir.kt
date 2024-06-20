package com.deenislamic.sdk.service.network.response.quran.qurangm.ayat

import androidx.annotation.Keep

@Keep
internal data class Tafsir(
    val Id: Int,
    val JuzId: Int,
    val Language: String,
    val Reference: String,
    val SurahId: Int,
    val Text: String,
    val TranslatorId: Int,
    val VerseId: Int
)