package com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz

import androidx.annotation.Keep

@Keep
internal data class Option(
    val Description: String,
    val IsCorrect: Boolean,
    val OptionId: Int,
    val selected:Boolean = false
)