package com.deenislam.sdk.service.network.response.quran.juz

import android.os.Parcelable
import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.quran.juz.Juz
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
internal data class JuzResponse(
    val juzs: List<Juz>
) : Parcelable