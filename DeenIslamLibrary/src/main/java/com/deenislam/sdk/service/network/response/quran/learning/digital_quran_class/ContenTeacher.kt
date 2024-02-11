package com.deenislamic.service.network.response.quran.learning.digital_quran_class

import androidx.annotation.Keep

@Keep
data class ContenTeacher(
    val TeacherId: Int,
    val TeacherImageUrl: String,
    val TeacherIntro: String,
    val TeacherName: String
)