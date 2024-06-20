package com.deenislamic.sdk.service.network.response.quran.qurangm.ayat

import androidx.annotation.Keep

@Keep
internal data class SurahInfo(
    val Ruku_number: Int,
    val SurahName: String,
    val SurahNameInArabic: String,
    val SurahNameMeaning: String,
    val SurahOrigin: String,
    val TotalAyat: Int
)