package com.deenislam.sdk.service.callback.quran

import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.Data

internal interface QuranPlayerCallback {
    fun isQuranPlaying(position: Int, duration: Long?, totalAyat: Int){

    }
    fun isQuranPause(){

    }
    fun isQuranStop(){

    }
    fun isQuranComplete(){

    }

    fun updateSurahDetails(currentSurahDetails: Data){

    }

    fun globalMiniPlayerClosed(){

    }
}