package com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.Data

@Keep
internal data class DigitalQuranClassResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)