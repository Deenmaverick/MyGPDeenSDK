package com.deenislamic.sdk.service.network.response.quran.qurangm.ayat

import androidx.annotation.Keep

@Keep
internal data class Translation(
    val Id: Int,
    val JuzId: Int,
    val Language: String,
    val SurahId: Int,
    val Translation: String,
    val TranslatorId: Int,
    val VerseId: Int
)