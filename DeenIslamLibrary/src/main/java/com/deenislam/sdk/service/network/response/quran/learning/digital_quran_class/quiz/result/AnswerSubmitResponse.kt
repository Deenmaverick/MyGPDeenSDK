package com.deenislamic.service.network.response.quran.learning.digital_quran_class.quiz.result

import androidx.annotation.Keep

@Keep
data class AnswerSubmitResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)