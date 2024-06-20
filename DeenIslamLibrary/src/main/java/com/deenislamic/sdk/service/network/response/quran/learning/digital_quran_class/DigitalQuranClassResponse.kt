package com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class

import androidx.annotation.Keep

@Keep
internal data class DigitalQuranClassResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)