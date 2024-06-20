package com.deenislamic.sdk.service.network.response.allah99name

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
internal data class Data(
    val Arabic: String,
    val ContentUrl: String,
    val Fazilat: String,
    val Id: Int,
    val ImageUrl: String,
    val Meaning: String,
    val Name: String,
    val Serial: Int
):Parcelable