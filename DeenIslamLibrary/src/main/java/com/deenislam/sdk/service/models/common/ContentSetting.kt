package com.deenislam.sdk.service.models.common

import androidx.annotation.Keep

@Keep
internal data class ContentSetting(
    var arabicFontSize:Float = 0F,
    var banglaFontSize:Float = 0F,
    var arabicFont:Int = 1
)
