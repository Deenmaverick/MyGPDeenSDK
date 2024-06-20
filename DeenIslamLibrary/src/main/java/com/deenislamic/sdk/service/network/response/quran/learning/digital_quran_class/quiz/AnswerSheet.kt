package com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.quiz

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
internal data class AnswerSheet(
    val contentID: Int,
    val courseID: Int,
    val language: String,
    val optionID: Int,
    val playtime: Int=0,
    val questionID: Int
):Parcelable