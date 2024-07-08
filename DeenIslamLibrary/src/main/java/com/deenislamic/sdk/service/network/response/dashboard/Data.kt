package com.deenislamic.sdk.service.network.response.dashboard

import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data

@Keep
internal data class Data(
    val Design: String="",
    val AppDesign:String? = null,
    val Logo:String ? = null,
    val Id: Int=0,
    val Items: List<Item> = arrayListOf(),
    val Sequence: Int=0,
    val Title: String="",
    val subContentData: Data? = null,
    var isloaded:Boolean = false
)