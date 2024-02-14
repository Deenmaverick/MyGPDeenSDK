package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.quran.qurangm.ayat.Ayath
import com.deenislam.sdk.views.adapters.quran.AlQuranAyatAdapter

internal interface AlQuranAyatCallback
{
    fun playNextAyat(position: Int)
    fun isAyatPlaying(position: Int, duration: Long?)
    fun isAyatPause(byService: Boolean=false)

    fun ayatFavClicked(getAyatData: Ayath, absoluteAdapterPosition: Int)

    fun isLoadingState(b: Boolean)
    fun playNextSurah(byService: Boolean)
    fun playPrevSurah(byService: Boolean)

    fun tafsirBtnClicked(surahId: Int, verseId: Int, ayatArabic: String, arabicFont: Int)

    fun startPlayingQuran(data: ArrayList<Ayath>, pos: Int)
    {

    }

    fun setAdapterCallback(viewHolder: AlQuranAyatAdapter.ViewHolder)
    {

    }

    //fun customShareAyat(enText: String, bnText: String, arText: String, verseKey: String)

}