package com.deenislamic.service.network.response.quran.learning.digital_quran_class

import androidx.annotation.Keep

@Keep
data class Course(
    val ActualPrice: String,
    val CourseImageUrl: String,
    val CourseName: String,
    val Description: String,
    val DiscountPrice: String,
    val TotalTrack: String,
    val isBestSeller: Boolean,
    val isCertificate: Boolean,
    val isSubscribed:Boolean
)