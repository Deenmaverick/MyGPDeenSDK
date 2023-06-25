package com.deenislam.sdk.service.network.response.quran.surah_details

data class SurahDetailsData(
    val AyathList: List<Ayath>,
    val IsNextEnabled: Boolean,
    val IsPreviousEnabled: Boolean
)