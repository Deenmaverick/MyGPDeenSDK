package com.deenislamic.sdk.service.network.response.quran.surah_details

import androidx.annotation.Keep

@Keep
internal data class SurahDetails(
    val Data: SurahDetailsData,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)