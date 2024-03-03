package com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.quiz

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.quiz.Data

@Keep
internal data class QuranCLassQuizQuestionResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)