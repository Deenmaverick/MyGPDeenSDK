package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.service.models.quran.quranplayer.FontList
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Ayath
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Qari
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.TafsirList
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Translator
import com.deenislamic.sdk.views.adapters.quran.AlQuranAyatAdapter

internal interface AlQuranAyatCallback
{
    fun playNextAyat(position: Int){}
    fun isAyatPlaying(position: Int, duration: Long?){}
    fun isAyatPause(byService: Boolean=false){}

    fun ayatFavClicked(getAyatData: Ayath, absoluteAdapterPosition: Int){}

    fun isLoadingState(b: Boolean){}
    fun playNextSurah(byService: Boolean){}
    fun playNextJuz(byService: Boolean){}
    fun playPrevSurah(byService: Boolean){}
    fun playPrevJuz(byService: Boolean){}
    fun tafsirBtnClicked(surahId: Int, verseId: Int, ayatArabic: String, arabicFont: Int){}

    fun startPlayingQuran(data: ArrayList<Ayath>, pos: Int)
    {

    }

    fun setAdapterCallback(viewHolder: AlQuranAyatAdapter.ViewHolder)
    {

    }

    //fun customShareAyat(enText: String, bnText: String, arText: String, verseKey: String)

    fun dialog_select_arabic_font()
    {

    }

    fun getArabicFontList(): ArrayList<FontList>{
        return arrayListOf()
    }

    fun getTranslatorData():ArrayList<Translator>{
        return arrayListOf()
    }

    fun getqariList():ArrayList<Qari>{
        return  arrayListOf()
    }

    fun getTafsirList():ArrayList<TafsirList>{
        return arrayListOf()
    }

    fun dialog_select_tafsirMaker(){}

    fun dialog_select_translator(lang: String) {

    }

    fun dialog_select_reciter(){

    }

    fun customShareAyat(enText: String, bnText: String, arText: String, verseKey: String){}

    fun option3dotClicked(getdata: OptionList,ayathData:Ayath?=null) {

    }

    fun option3dotClicked(getdata: Ayath) {

    }

    fun zoomBtnClickedADP(){

    }

}