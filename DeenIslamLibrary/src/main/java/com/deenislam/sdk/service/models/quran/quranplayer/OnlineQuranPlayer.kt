package com.deenislam.sdk.service.models.quran.quranplayer

import com.deenislam.sdk.service.network.response.quran.qurangm.ayat.Ayath
import com.deenislam.sdk.service.network.response.quran.qurangm.ayat.Qari
import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.Data

internal data class OnlineQuranPlayer(
    val selectedQari:Int,
    var qarisData: ArrayList<Qari>,
    var ayatList:ArrayList<Ayath>,
    val surahID:Int,
    val surahList:ArrayList<Data>,
    val currentlyPlayingPos:Int,
    val totalAyat:Int,
    val currentPageNo:Int,
    val retryFetchNetwork:Int,
    val retryPlayAudio:Int,
    val curProgress:Int,
    val formattedTime:String,
    val quranJuzList:ArrayList<com.deenislam.sdk.service.network.response.quran.qurangm.paralist.Data>
)
