package com.deenislam.sdk.service.models.quran

import com.deenislam.sdk.service.network.response.quran.surah_details.SurahDetailsData

interface AlQuranResource {

    data class surahDetails(val data:SurahDetailsData):AlQuranResource
}