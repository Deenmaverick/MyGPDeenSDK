package com.deenislam.sdk.service.network.response.quran.juz

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Juz(
    val first_verse_id: Int,
    val id: Int,
    val juz_number: Int,
    val last_verse_id: Int,
    val verse_mapping: VerseMapping,
    val verses_count: Int
) :Parcelable