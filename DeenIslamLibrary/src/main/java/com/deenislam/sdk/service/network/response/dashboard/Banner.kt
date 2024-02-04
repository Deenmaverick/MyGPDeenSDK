package com.deenislam.sdk.service.network.response.dashboard

import androidx.annotation.Keep

@Keep
internal data class Banner(
    val ArabicText: String,
    val FeatureID: Int,
    val Id: Int,
    val IsActive: Any,
    val Language: Any,
    val Reference: Any,
    val Text: String,
    val Title: String,
    val contentBaseUrl: String,
    val imageurl1: String,
    val imageurl2: Any,
    val imageurl3: Any,
    val imageurl4: Any,
    val imageurl5: Any,
    val MText:String,
    val ContentType:String
)