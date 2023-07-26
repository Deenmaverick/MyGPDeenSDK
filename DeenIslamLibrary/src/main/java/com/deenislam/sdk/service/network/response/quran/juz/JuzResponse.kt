package com.deenislam.sdk.service.network.response.quran.juz

import android.os.Parcelable
import com.deenislam.sdk.service.network.response.quran.juz.Juz
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JuzResponse(
    val juzs: List<Juz>
) : Parcelable