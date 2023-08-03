package com.deenislam.sdk.service.network.response.dashboard

internal data class Data(
    val ActiveItems: List<String>,
    val Banners: List<Banner>,
    val VerseData: DailyVerse,
    val DailyDua: List<DailyDua>,
    val Hadith: Hadith,
    val IslamicName: IslamicName,
    val Qibla: List<Qibla>,
    val Services: List<Service>,
    val Tasbeeh: Tasbeeh,
    val Zakat: Zakat
)