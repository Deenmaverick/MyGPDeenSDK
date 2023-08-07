package com.deenislam.sdk.service.network.response.quran

import android.os.Parcelable
import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
internal data class SurahList(
    val Data: List<Chapter>,
    val Message: String ="",
    val Success: Boolean = false,
    val TotalData: Int = 0
) : Parcelable