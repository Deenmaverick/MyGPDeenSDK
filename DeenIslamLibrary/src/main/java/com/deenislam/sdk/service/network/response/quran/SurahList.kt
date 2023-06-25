package com.deenislam.sdk.service.network.response.quran

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SurahList(
    val Data: List<SurahListData>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
) : Parcelable