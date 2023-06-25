package com.deenislam.sdk.service.network.response.quran.surah_details

internal data class Ayath(
    val AyatOrder: Int,
    val ContentBaseUrl: String,
    val ContentUrl: String,
    val Duration: String,
    val Language: String,
    val Name: String,
    val NameInArabic: String,
    val Origin: String,
    val SurahNo: Int,
    val Text: String,
    val TextInArabic: String,
    val TotalAyat: Int
)