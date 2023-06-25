package com.deenislam.sdk.service.network.response.quran.qurannew.surah

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class TranslatedName(
    val language_name: String,
    val name: String
) : Parcelable