package com.deenislamic.sdk.service.network.response.islamicname

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    var IsFavorite: Boolean,
    val Name: String,
    val gender: String,
    val isactive: Boolean,
    val language: String,
    val meaning: String,
    val nameinarabic: String,
    val pronunciation: String
)