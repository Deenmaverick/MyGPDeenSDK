package com.deenislamic.sdk.service.network.response.share

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val category: Int,
    val contenturl: String,
    val imageurl: String,
    val isactive: Boolean,
    val language: String,
    val pronunciation: String,
    val reference: String,
    val referenceurl: String,
    val serial: Int,
    val text: String,
    val textinarabic: String,
    val title: String
)