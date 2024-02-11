package com.deenislamic.service.network.response.quran.learning.digital_quran_class.quiz.result

import androidx.annotation.Keep

@Keep
data class Data(
    val NoOfCorrectAnswer: Int,
    val NoOfQuestions: Int,
    val Playtime: Double,
    val Score: Int
)