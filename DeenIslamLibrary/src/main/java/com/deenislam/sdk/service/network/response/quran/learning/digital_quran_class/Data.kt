package com.deenislamic.service.network.response.quran.learning.digital_quran_class

import androidx.annotation.Keep

@Keep
data class Data(
    val contenTeacher: ContenTeacher,
    val course: Course,
    val courseContens: List<CourseConten>
)