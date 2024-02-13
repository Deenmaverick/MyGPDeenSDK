package com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.ContenTeacher
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.Course
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.CourseConten

@Keep
internal data class Data(
    val contenTeacher: ContenTeacher,
    val course: Course,
    val courseContens: List<CourseConten>
)