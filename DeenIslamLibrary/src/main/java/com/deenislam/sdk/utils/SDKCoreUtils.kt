package com.deenislam.sdk.utils

import com.deenislam.sdk.R

fun String.getRCDestination():Int =
    when(this)
    {
        "quran" -> R.id.quranFragment
        "hadith" -> R.id.hadithFragment
        "prayer_time" -> R.id.prayerTimesFragment
        "dua" -> R.id.dailyDuaFragment
        "zakat" -> R.id.zakatFragment
        "tasbeeh" -> R.id.tasbeehFragment
        "compass" -> R.id.compassFragment
        "islamic_name" -> R.id.islamicNameFragment
        else -> 0
    }