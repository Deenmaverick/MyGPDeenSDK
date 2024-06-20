package com.deenislamic.sdk.service.network.response.quran.qurangm.ayat

import androidx.annotation.Keep

@Keep
internal data class Qari(
    val TranslatorId: Int,
    val category: String,
    val contentFolder: String,
    val imageurl: String?,
    val language: String,
    val reference: String,
    val text: String,
    val textEnglish: String,
    val title: Int,
    var isSelected:Boolean = false
)