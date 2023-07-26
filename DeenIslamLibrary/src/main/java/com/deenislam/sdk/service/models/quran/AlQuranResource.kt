package com.deenislam.sdk.service.models.quran

import com.deenislam.sdk.service.network.response.quran.juz.Juz
import com.deenislam.sdk.service.network.response.quran.surah_details.SurahDetailsData
import com.deenislam.service.network.response.quran.verses.Verse

internal interface AlQuranResource {

    data class surahDetails(val data:SurahDetailsData):AlQuranResource
    data class versesByChapter(val data: List<Verse>, val nextPage: Int?) :AlQuranResource
    data class juzList(val juzs: List<Juz>) :AlQuranResource
}