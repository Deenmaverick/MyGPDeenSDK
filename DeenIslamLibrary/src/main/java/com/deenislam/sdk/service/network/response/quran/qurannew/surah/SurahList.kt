package com.deenislam.sdk.service.network.response.quran.qurannew.surah

import android.os.Parcelable
import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
internal data class SurahList(
    val chapters: List<Chapter>
) : Parcelable