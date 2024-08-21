package com.deenislamic.sdk.views.dashboard.patch;

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.TasbeehCallback
import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.service.database.entity.UserPref
import com.deenislamic.sdk.service.libs.media3.AudioManager
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.FullCircleGaugeView
import com.deenislamic.sdk.utils.numberLocale
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal class Tasbeeh(itemView: View) {

    private var callback = CallBackProvider.get<TasbeehCallback>()
    private val itemTitle:AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private val duaArabicTxt: AppCompatTextView = itemView.findViewById(R.id.duaArabicTxt)
    private val duaTxt: AppCompatTextView = itemView.findViewById(R.id.duaTxt)
    private val playBtn: MaterialButton = itemView.findViewById(R.id.playBtn)
    private val leftBtn: AppCompatImageView = itemView.findViewById(R.id.leftBtn)
    private val rightBtn: AppCompatImageView = itemView.findViewById(R.id.rightBtn)
    private val countBtn: MaterialButton = itemView.findViewById(R.id.countBtn)
    private val targetCountTxt:AppCompatTextView = itemView.findViewById(R.id.targetCountTxt)
    private val countView: FullCircleGaugeView = itemView.findViewById(R.id.countView)
    private val countTxt:AppCompatTextView = itemView.findViewById(R.id.countTxt)
    private val countViewLoading: LinearLayout = itemView.findViewById(R.id.countViewLoading)
    private val btnShare:MaterialButton = itemView.findViewById(R.id.btnShare)
    private var tasbeehdata: Tasbeeh? = null
    private var userPref: UserPref? = null
    private var todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
    var selectedPos:Int = 0
    private var selectedCount:Int = 1
    private var track4Count:Int = 0

    private val audioManager: AudioManager = AudioManager().getInstance()


    val duaList:ArrayList<Tasbeeh> = arrayListOf(
        Tasbeeh(duaid=1,dua="Subhan Allah", dua_bn = "সুবহান আল্লাহ", dua_arb = "سُبْحَانَ ٱللَّ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Subhanallah.mp3"),
        Tasbeeh(duaid=2,dua="Alhamdulillah", dua_bn = "আলহামদুলিল্লাহ", dua_arb = "ٱلْحَمْدُ لِلَّهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Alhamdulihhah.mp3"),
        Tasbeeh(duaid=3,dua="Bismillah", dua_bn = "বিসমিল্লাহ", dua_arb = "بِسْمِ اللهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Bismillah.mp3"),
        Tasbeeh(duaid=4,dua="Allahu Akbar", dua_bn = "আল্লাহু আকবার", dua_arb = "الله أكبر",audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Allahuakbar.mp3"),
        Tasbeeh(duaid=5,dua="Astagfirullah", dua_bn = "আস্তাগফিরুল্লাহ", dua_arb = "أَسْتَغْفِرُ اللّٰهَ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Astagfirullah.mp3"),
        Tasbeeh(duaid=6,dua="Allah", dua_bn = "আল্লাহ", dua_arb = "الله",audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Allahu.mp3"),
        Tasbeeh(duaid=7,dua="La Ilaha illa Allah", dua_bn = "লা ইলাহা ইল্লাল্লাহ", dua_arb = "لَا إِلَٰهَ إِلَّا ٱللَّ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Lailaha_Illalahu.mp3"),
        Tasbeeh(duaid=8,dua="Subhanallahi Wa-Bihamdihi", dua_bn = "সুবহানাল্লাহি ওয়া-বিহামদিহি", dua_arb = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Subhanallahi_Wa_Bihamdihi.mp3")
    )


    fun load(data: Data)
    {
        itemTitle.text = data.Title
        callback = CallBackProvider.get<TasbeehCallback>()
        setDuaText()
        countBtn.setOnClickListener {
            //it.isEnabled = false
            if(userPref?.tasbeeh_sound==true) {
                CoroutineScope(Dispatchers.IO).launch{
                    audioManager.playRawAudioFile(countBtn.context, R.raw.tasbeeh_count, isCallback = false)
                }
            }

            tasbeehdata?.let { it1 -> callback?.tasbeehSetCount(selectedCount, it1,todayDate) }

        }

        playBtn.setOnClickListener {

            audioManager.playAudioFromUrl(duaList[selectedPos].audioUrl, isCallback = false)
        }


        leftBtn.setOnClickListener {
            callback = CallBackProvider.get<TasbeehCallback>()
            callback?.let {
                if(selectedPos==0)
                    selectedPos = duaList.size-1
                else
                    selectedPos -= 1

                track4Count = 0
                //countViewLoading.show()
                it.tasbeehLoadApi(selectedPos,todayDate)
                setDuaText()
            }

        }

        rightBtn.setOnClickListener {
            callback = CallBackProvider.get<TasbeehCallback>()
            callback?.let {
                if(selectedPos==duaList.size-1)
                    selectedPos = 0
                else
                    selectedPos++
                track4Count = 0
                //countViewLoading.show()
                it.tasbeehLoadApi(selectedPos, todayDate)
                setDuaText()
            }
        }

        btnShare.setOnClickListener {
            callback = CallBackProvider.get<TasbeehCallback>()
            callback?.tasbeehShare(duaArabicTxt.text.toString(),duaTxt.text.toString())
        }

        callback?.tasbeehLoadApi(selectedPos, todayDate)

    }

    private fun setDuaText()
    {
        duaArabicTxt.text = duaList[selectedPos].dua_arb
        duaTxt.text =  if(DeenSDKCore.GetDeenLanguage() == "en")
            duaList[selectedPos].dua
        else
            duaList[selectedPos].dua_bn
    }

    private fun updateCountView(count:Int)
    {

        tasbeehdata?.apply {

            var trackcount = -1

            var targetCount = -1

            when (count) {
                1 -> {
                    trackcount = this.track1
                    targetCount = 33
                }
                2 -> {
                    trackcount = this.track2
                    targetCount = 34
                }
                3 -> {
                    trackcount = this.track3
                    targetCount = 99
                }
                else -> {
                    trackcount = track4Count++
                }
            }

            if(trackcount>=targetCount && targetCount!=-1)
            {
                countView.setProgress(100F)
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/${targetCount}".numberLocale()
                //countBtn.text = itemView.context.getString(R.string.count_input,targetCount.toString().numberLocale())
            }
            else if(trackcount<=targetCount && count!=-1)
            {
                countView.setProgress(
                    if((trackcount.toFloat()*100)/targetCount>100)100F
                    else if((trackcount.toFloat()*100)/targetCount<0)0F
                    else (trackcount.toFloat()*100)/targetCount
                )
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/${targetCount}".numberLocale()
                //countBtn.text = itemView.context.getString(R.string.count_input,targetCount.toString().numberLocale())
            }
            else {
                countView.setProgress(100F)
                countTxt.text = trackcount.toString().numberLocale()
                targetCountTxt.text = "/∞"
                //countBtn.text = "∞"
            }
        }
    }

     fun tasbeehViewState(data: Tasbeeh)
    {

        countBtn.isEnabled = true
        //countViewLoading.hide()
        tasbeehdata = data
        Log.e("TasbeehviewState",Gson().toJson(tasbeehdata))
        updateCountView(selectedCount)
    }

    fun soundViewState(userPref: UserPref?) {
        this.userPref = userPref
    }


}