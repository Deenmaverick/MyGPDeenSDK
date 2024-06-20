package com.deenislamic.sdk.service.network.response.islamiceducationvideo

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
internal data class Content(
    val Id: Int,
    val category: String,
    val categoryID: Int,
    val duration: String,
    val imageurl: String,
    val reference: String,
    val referenceurl: String,
    val title: String,
    val videourl: String
) : Parcelable