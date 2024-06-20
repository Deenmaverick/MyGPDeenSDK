package com.deenislamic.sdk.service.network.response.quran.juz

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
internal data class Juz(
    val first_verse_id: Int,
    val id: Int,
    val juz_number: Int,
    val last_verse_id: Int,
    val verse_mapping: VerseMapping,
    val verses_count: Int
) :Parcelable