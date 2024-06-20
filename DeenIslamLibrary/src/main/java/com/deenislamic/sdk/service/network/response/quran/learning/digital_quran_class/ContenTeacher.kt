package com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class

import androidx.annotation.Keep

@Keep
internal data class ContenTeacher(
    val TeacherId: Int,
    val TeacherImageUrl: String,
    val TeacherIntro: String,
    val TeacherName: String
)