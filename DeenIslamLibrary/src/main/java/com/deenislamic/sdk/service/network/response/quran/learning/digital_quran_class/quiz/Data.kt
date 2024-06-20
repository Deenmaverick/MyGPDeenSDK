package com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz

import androidx.annotation.Keep

@Keep
internal data class Data(
    val QuestionID: Int,
    val ContentID: Int,
    val CourseID: Int,
    val IsActive: Boolean,
    val Language: String,
    val OnTime: String,
    val Options: List<Option>,
    val QuestionImage: Any,
    val QuestionScore: Int,
    val QuestionTitle: String,
    val Quiztype: Int
)