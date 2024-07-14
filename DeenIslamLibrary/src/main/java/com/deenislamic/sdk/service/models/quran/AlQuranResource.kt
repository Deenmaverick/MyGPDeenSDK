package com.deenislamic.sdk.service.models.quran

import com.deenislamic.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.AyatResponse
import com.deenislamic.sdk.service.network.response.quran.qurangm.paralist.ParaListResponse
import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.SurahListResponse
import com.deenislamic.sdk.service.network.response.quran.surah_details.SurahDetailsData
import com.deenislamic.sdk.service.network.response.quran.tafsir.Data

internal interface AlQuranResource {

    data class surahDetails(val data:SurahDetailsData):AlQuranResource
    data class VersesByChapter(val data: AyatResponse, val isReadingMode: Boolean) :AlQuranResource

    data class QuranHomePatch(val data: DashboardResponse) :AlQuranResource
    data class SurahList(val data: SurahListResponse) :AlQuranResource
    data class ParaList(val data: ParaListResponse) :AlQuranResource

    data class ayatFav(val isFav: Boolean, val position: Int) :AlQuranResource
    data class Tafsir(val data: List<Data>, val ayatArabic: String, val arabicFont: Int) :AlQuranResource
}