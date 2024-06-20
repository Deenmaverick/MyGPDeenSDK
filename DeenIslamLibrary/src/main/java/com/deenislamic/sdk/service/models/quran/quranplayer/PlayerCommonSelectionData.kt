package com.deenislamic.sdk.service.models.quran.quranplayer

internal data class PlayerCommonSelectionData(
    val Id: Int,
    val imageurl: String?,
    val title: String?,
    val language:String="",
    var isSelected:Boolean = false
)
