package com.deenislam.sdk.service.network.response.quran.tafsir

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.quran.tafsir.Data

@Keep
internal data class TafsirResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)