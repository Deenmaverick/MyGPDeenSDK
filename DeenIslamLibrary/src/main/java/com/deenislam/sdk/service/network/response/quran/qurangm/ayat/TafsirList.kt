package com.deenislam.sdk.service.network.response.quran.qurangm.ayat

import androidx.annotation.Keep

@Keep
internal data class TafsirList(
    val TranslatorId: Int,
    val category: String,
    val contentFolder: Any,
    val imageurl: String,
    val language: String,
    val reference: String,
    val text: String,
    val textEnglish: String,
    val title: Int
)