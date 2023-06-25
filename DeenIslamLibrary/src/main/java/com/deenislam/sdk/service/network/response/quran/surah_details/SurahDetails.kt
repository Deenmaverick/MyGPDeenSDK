package com.deenislam.sdk.service.network.response.quran.surah_details

internal data class SurahDetails(
    val Data: SurahDetailsData,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)