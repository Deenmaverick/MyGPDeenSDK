package com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class

import androidx.annotation.Keep

@Keep
internal data class CourseConten(
    val BannerURL: String,
    val ContenTtitle: String,
    val ContentId: Int,
    val CourseId: Int,
    val Duration: String,
    val DurationInSeconds: Int,
    val FilePath: String,
    val IsComplete: Boolean,
    val IsPassed: Boolean,
    val IsQuiz: Boolean,
    val Language: String,
    val PreviewURL: String,
    val Serial: Int,
    val ShowQuiz: Boolean,
    val Status: Boolean,
    val WatchDuration: Int,
    val isPlaying:Boolean = false
)