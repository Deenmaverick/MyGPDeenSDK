package com.deenislam.sdk.service.network.response.dashboard

import androidx.annotation.Keep

@Keep
internal data class Hadith(
    val ArabicText: Any,
    val FeatureID: Int,
    val Id: Int,
    val IsActive: Any,
    val Language: Any,
    val Reference: String?,
    val HadithText: String,
    val HadithArabicText: String,
    val Title: String,
    val contentBaseUrl: String,
    val imageurl1: String,
    val imageurl2: Any,
    val imageurl3: Any,
    val imageurl4: Any,
    val imageurl5: Any
)