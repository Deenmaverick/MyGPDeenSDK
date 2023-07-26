package com.deenislam.sdk.service.network.response.quran.qurannew.surah

import android.os.Parcelable
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import kotlinx.parcelize.Parcelize

@Parcelize
data class SurahList(
    val chapters: List<Chapter>
) : Parcelable