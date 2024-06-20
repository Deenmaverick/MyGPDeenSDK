package com.deenislamic.sdk.service.network.response.advertisement

import androidx.annotation.Keep

@Keep
internal data class Data(
    val ContentType: String,
    val DelayDuration: Int,
    val Id: Int,
    val SkipDuration: Int,
    val buttontext: String,
    val categoryId: Int,
    val categoryName: String,
    val contenturl: String,
    val duration: Int,
    val imagesize: String,
    val imageurl: String,
    val redirecturl: String,
    val text: String,
    val title: String
)