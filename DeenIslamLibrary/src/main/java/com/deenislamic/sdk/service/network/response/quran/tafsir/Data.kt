package com.deenislamic.sdk.service.network.response.quran.tafsir

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val JuzId: Int,
    val Language: String,
    val Reference: String,
    val SurahId: Int,
    val Text: String,
    val TranslatorId: Int,
    val VerseId: Int
)