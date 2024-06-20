package com.deenislamic.sdk.service.network.response.quran.qurangm.ayat

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Ayaths: List<Ayath>,
    val Qaris: List<Qari>,
    val Tafsirs: List<TafsirList>,
    val Translators: List<Translator>,
    val SurahInfo: SurahInfo?
)