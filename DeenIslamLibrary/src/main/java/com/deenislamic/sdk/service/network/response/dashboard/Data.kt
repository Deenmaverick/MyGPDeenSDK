package com.deenislamic.sdk.service.network.response.dashboard

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Design: String,
    val AppDesign:String? = null,
    val Logo:String ? = null,
    val Id: Int,
    val Items: List<Item>,
    val Sequence: Int,
    val Title: String
)