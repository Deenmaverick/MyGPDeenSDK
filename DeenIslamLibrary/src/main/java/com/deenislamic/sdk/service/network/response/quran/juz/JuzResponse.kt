package com.deenislamic.sdk.service.network.response.quran.juz

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
internal data class JuzResponse(
    val juzs: List<Juz>
) : Parcelable