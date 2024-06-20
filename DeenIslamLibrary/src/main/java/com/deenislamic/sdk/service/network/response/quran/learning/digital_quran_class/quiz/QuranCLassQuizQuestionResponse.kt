package com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz

import androidx.annotation.Keep

@Keep
internal data class QuranCLassQuizQuestionResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)