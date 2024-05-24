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
        "ramadan_regular" -> R.id.ramadanOtherDayFragment
        "asmaul_husna" -> R.id.allah99NamesFragment
        "prayer_learning" -> R.id.prayerLearningFragment
        "nearest_mosque" -> R.id.nearestMosqueWebviewFragment
        "podcast" -> R.id.livePodcastFragment
        "live_makkah_madina" -> R.id.makkahLiveFragment
        "islamic_golpo" -> R.id.islamicEducationVideoHomeFragment
        "islamic_event" -> R.id.islamicEventHomeFragment
        "quran_class" -> R.id.quranLearningFragment
        "islamic_boyan" -> R.id.islamicBoyanHomeFragment
        "khatam_e_quran" -> R.id.khatamEQuranHomeFragment
        "qurbani" -> R.id.qurbaniFragment
        else -> 0
    }