package com.deenislamic.sdk.views.adapters.quran;

import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.databinding.ItemQuranAyatBinding
import com.deenislamic.sdk.databinding.ItemQuranReadingBinding
import com.deenislamic.sdk.service.callback.AlQuranAyatCallback
import com.deenislamic.sdk.service.libs.media3.APAdapterCallback
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Ayath
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Data
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Qari
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.views.base.BaseViewHolderBinding
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.invisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val LIST_MODE = 0
private const val READING_MODE = 1
internal class AlQuranAyatAdapter(
    private val callback: AlQuranAyatCallback,
    private var isReadingMode: Boolean = false,
    private var isBnReading:Boolean = false
) :  RecyclerView.Adapter<BaseViewHolderBinding>() {

    private var data: ArrayList<Ayath> = arrayListOf()
    private var qarisData: ArrayList<Qari> = arrayListOf()
    private var audioFolderLocation = ""
    private var previousCallbackPosition:Int = -1
    private var isPlaying:Boolean = false
    private var isMiniPlayerCall:Boolean = false
    private var targetSpannableOffset: Int = 0

    // player setting
    private var theme_font_size:Float = if(isBnReading) 18F else 24F
    private var english_font_size:Float = 14F
    private var translation_font_size:Float = 14F
    private var setting_transliteration = true
    private var setting_bn_meaning = true
    private var auto_play_next = true
    private var arabicFont:Int = 1
    private var selectedQari = 931
    private var en_translator = 131
    private var bn_translator = 161
    private var surahID = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolderBinding =

        when(viewType)
        {
            READING_MODE ->  ViewHolder(
                binding_read = ItemQuranReadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            LIST_MODE -> {

                Log.e("LIST_MODE","called")
                ViewHolder(
                    binding_list = ItemQuranAyatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }




    fun clear()
    {
        data.clear()
        previousCallbackPosition = -1
        isPlaying = false
        notifyDataSetChanged()
    }

    fun updateMode(isReadingMode: Boolean)
    {
        this.isReadingMode = isReadingMode
        notifyDataSetChanged()
    }

    fun updateReadingMode(isBnReading: Boolean)
    {
        this.isBnReading = isBnReading

    }

    fun updateQari(qari:Int)
    {
        if(qari!=1)
            selectedQari = qari

    }

    fun getSelectedQari() = selectedQari

    fun updateFavAyat(fav: Boolean, position: Int)
    {
        data[position].IsFavorite = fav
        notifyItemChanged(position)
    }

    fun updateEnTranslator(translatorID:Int)
    {
        if(translatorID!=0)
            en_translator = translatorID

    }

    fun update_bn_meaning(bol:Boolean)
    {
        setting_bn_meaning = bol
        //notifyDataSetChanged()
    }

    fun getEnTranslator() = en_translator

    fun updateBnTranslator(translatorID:Int)
    {
        if(translatorID!=0)
            bn_translator = translatorID

    }

    /*  fun updatePlayerLoading(bol:Boolean)
      {
          updatePlayerLoading(bol)
      }*/

    fun getBnTranslator() = bn_translator

    fun isMediaPlaying(position: Int)
    {
        if(previousCallbackPosition>=0 && previousCallbackPosition!=position) {
            isPlaying = false
            notifyItemChanged(previousCallbackPosition)
        }
        if(position>=0) {
            isPlaying = true
            notifyItemChanged(position)
        }
    }

    fun isMediaPause(position: Int, byService: Boolean=false)
    {
        isPlaying = false
        callback.isAyatPause(byService)
        if(previousCallbackPosition>=0 && previousCallbackPosition!=position) {
            notifyItemChanged(previousCallbackPosition)
            //notifyDataSetChanged()
        }
        if(position>=0)
        //notifyDataSetChanged()
            notifyItemChanged(position)

    }

    fun miniPlayerCall(expand:Boolean = false,byService: Boolean = false)
    {

        //isPlaying = MainActivityDeenSDK.instance?.isQuranMiniPlayerRunning()?:false

        if(expand)
        {
            if(!isPlaying)
            {
                if (previousCallbackPosition < 0)
                    previousCallbackPosition = 0

                isPlaying = true
                isMiniPlayerCall = !byService

                if (isReadingMode)
                    notifyItemChanged(0)
                else
                    notifyItemChanged(previousCallbackPosition)

            }
        }
        else {

            if (isPlaying) {
                callback.isAyatPause(byService)
                isPlaying = false

            } else {
                if (previousCallbackPosition < 0)
                    previousCallbackPosition = 0

                isPlaying = true
                isMiniPlayerCall = !byService
            }

            if (previousCallbackPosition >= 0) {
                if (isReadingMode)
                    notifyItemChanged(0)
                else
                    notifyItemChanged(previousCallbackPosition)
            }
        }
    }

    fun miniPlayerPrevCall()
    {
        /* if(previousCallbackPosition>0)
         {

             // change already playing item
             isPlaying = false
             isMiniPlayerCall = true
             if(isReadingMode)
                 notifyItemChanged(0)
             notifyItemChanged(previousCallbackPosition)

             previousCallbackPosition--
             callback.isAyatPause()

             isPlaying = true
            //isMiniPlayerCall = true

             //notifyDataSetChanged()
             if(isReadingMode)
                 notifyItemChanged(0)
             notifyItemChanged(previousCallbackPosition)
         }
         else*/
        callback.playPrevSurah(true)

    }

    fun miniPlayerNextCall()
    {

        /*if(previousCallbackPosition+1<data.size) {

            isPlaying = false
            isMiniPlayerCall = true
            if(isReadingMode)
                notifyItemChanged(0)
            else
            notifyItemChanged(previousCallbackPosition)

            previousCallbackPosition++
            //AudioManager().getInstance().releasePlayer()
            callback.isAyatPause()

            isPlaying = true
            if(isReadingMode)
                notifyItemChanged(0)
            notifyItemChanged(previousCallbackPosition)

            //notifyDataSetChanged()
        }
        else*/
        callback.playNextSurah(true)

    }


    fun update(surahData: Data, surahID: Int)
    {

        CoroutineScope(Dispatchers.IO).launch {
            this@AlQuranAyatAdapter.surahID = surahID
            data.addAll(surahData.Ayaths)

            if(qarisData.isEmpty())
                qarisData.addAll(surahData.Qaris)

            withContext(Dispatchers.Main)
            {
                if(!isReadingMode)
                // notifyItemRangeInserted(data.size - surahData.size, surahData.size)
                    notifyItemInserted(itemCount)
                else
                    notifyItemRangeChanged(0,1)

            }
        }

    }

    fun update_theme_font_size(fontsize: Float)
    {
        theme_font_size = if(isBnReading)
            fontsize-6F
        else
            fontsize
        //notifyDataSetChanged()
    }

    fun update_english_font_size(fontsize: Float)
    {
        english_font_size = fontsize
        //notifyDataSetChanged()
    }

    fun update_translation_font_size(fontsize: Float)
    {
        translation_font_size = fontsize
        //notifyDataSetChanged()
    }

    fun getTranslationFontSize() = translation_font_size

    fun update_transliteration(bol:Boolean)
    {
        setting_transliteration = bol
        //notifyDataSetChanged()
    }

    fun isTransliterationEnable() = setting_transliteration

    fun update_auto_play_next(bol:Boolean)
    {
        auto_play_next = bol
        //notifyDataSetChanged()
    }

    fun isAutoPlay() = auto_play_next

    fun setArabicFont(font:Int)
    {
        arabicFont = font
        //notifyDataSetChanged()
    }

    fun getArabicFont() = arabicFont

    fun getDataSize() = data.size

    fun getTargetIndexOffset() = targetSpannableOffset

    private fun getQariWiseFolder(): String {
        Log.e("updateQari",selectedQari.toString())
        val idsToFilter = setOf(selectedQari)
        //if(audioFolderLocation.isEmpty())
        audioFolderLocation = qarisData.filter { it.title in idsToFilter }.getOrNull(0)?.contentFolder.toString()

        return audioFolderLocation

    }

    override fun getItemViewType(position: Int): Int {
        return if(isReadingMode)
            1
        else
            0
    }
    override fun getItemCount(): Int = if(isReadingMode) 1 else data.size

    override fun onBindViewHolder(holder: BaseViewHolderBinding, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }


    internal inner class ViewHolder(
        private val binding_list: ItemQuranAyatBinding? = null,
        private val binding_read: ItemQuranReadingBinding? = null
    ) : BaseViewHolderBinding(
        when {
            binding_list != null -> binding_list.root
            binding_read != null -> binding_read.root
            else -> null
        }

    ),
        APAdapterCallback {

        init {
            //AudioManager().getInstance().setupAdapterResponseCallback(this@ViewHolder)
            callback.setAdapterCallback(this@ViewHolder)
        }
        override fun onBind(position: Int , viewtype: Int) {
            super.onBind(position,viewtype)

            when(viewtype) {
                LIST_MODE -> {

                    val getAyatData = data[position]
                    var ayatArabic = ""
                    //var wordToHighlight = ""
                    val transliteration =
                        if(DeenSDKCore.GetDeenLanguage() == "en")
                            getAyatData.Transliteration_en.trim()
                        else
                            getAyatData.Transliteration_bn.trim()


                    when(arabicFont){

                        1-> {
                            val customFont = ResourcesCompat.getFont(itemView.context, R.font.indopakv2)
                            binding_list?.ayatArabic?.typeface = customFont
                            ayatArabic = " ${getAyatData.Arabic_indopak}"
                        }

                       /* 2-> {
                            val customFont = ResourcesCompat.getFont(itemView.context, R.font.kfgqpc_font)
                            binding_list?.ayatArabic?.typeface = customFont
                            ayatArabic = " ${getAyatData.Arabic_uthmani}"
                        }

                        3-> {
                            val customFont = ResourcesCompat.getFont(itemView.context, R.font.al_majed_quranic_font_regular)
                            binding_list?.ayatArabic?.typeface = customFont
                            ayatArabic = " ${getAyatData.Arabic_Custom}"
                        }*/
                    }


                    /*// hightlight word

        // Create a SpannableString from the full text
                    val spannableString = SpannableString(ayatArabic)

        // Find the start and end indices of the word to highlight
                    val startIndex = ayatArabic.indexOf(wordToHighlight)
                    val endIndex = startIndex + wordToHighlight.length

        // Apply a foreground color span to the word
                    val colorSpan = ForegroundColorSpan(Color.RED)
                    spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the modified spannable string to the TextView

                    binding.ayatArabic.text  = spannableString

                    binding.surayAyat.text = data[position].verse_key
        */

                    // binding.surayAyat.text = "${data[position].SurahNo}:${data[position].AyatOrder}"

                    if(!setting_transliteration)
                        binding_list?.transliteration?.hide()
                    else
                        binding_list?.transliteration?.show()

                    binding_list?.ayatBn?.visible(setting_bn_meaning)



                    binding_list?.ayatArabic?.text = ayatArabic
                    binding_list?.ayatArabic?.setTextSize(TypedValue.COMPLEX_UNIT_SP,theme_font_size)
                    binding_list?.transliteration?.text = transliteration
                    binding_list?.transliteration?.setTextSize(TypedValue.COMPLEX_UNIT_SP, if(DeenSDKCore.GetDeenLanguage() == "en") english_font_size else translation_font_size)

                    binding_list?.ayatEn?.setTextSize(TypedValue.COMPLEX_UNIT_SP,translation_font_size)
                    binding_list?.ayatEn?.setTextSize(TypedValue.COMPLEX_UNIT_SP,english_font_size)
                    binding_list?.ayatBn?.setTextSize(TypedValue.COMPLEX_UNIT_SP,translation_font_size)

                    val idsToFilter = setOf(en_translator,bn_translator)
                    val getTransalationData = getAyatData.Translations.filter { it.TranslatorId in idsToFilter }

                    if(getTransalationData.isNotEmpty()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding_list?.ayatEn?.text = Html.fromHtml(
                                getTransalationData[0].Translation,
                                Html.FROM_HTML_MODE_LEGACY
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            binding_list?.ayatEn?.text = Html.fromHtml(
                                getTransalationData[0].Translation
                            )
                        }

                    }
                    else
                        binding_list?.ayatEn?.hide()


                    if(getTransalationData.size>1) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding_list?.ayatBn?.text = Html.fromHtml(
                                getTransalationData[1].Translation,
                                Html.FROM_HTML_MODE_LEGACY
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            binding_list?.ayatBn?.text = Html.fromHtml(
                                getTransalationData[1].Translation
                            )
                        }

                    }
                    else
                        binding_list?.ayatBn?.hide()


                    binding_list?.surayAyat?.text = getAyatData.VerseKey

                    binding_list?.btnPlay?.setOnClickListener {
                        quranPlayBtnClick(absoluteAdapterPosition)
                    }

                    if (isPlaying && previousCallbackPosition == position) {

                        binding_list?.btnPlay?.setImageDrawable(
                            AppCompatResources.getDrawable(
                                binding_list.btnPlay.context,
                                R.drawable.ic_pause_fill
                            )
                        )

                        playLoadingState(false)

                        binding_list?.quranCard?.setCardBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_brand_accents_ultra_light
                            )
                        )

                        binding_list?.quranCard?.strokeWidth = 2.dp

                        binding_list?.ayatArabic?.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_primary
                            )
                        )
                        binding_list?.ayatBn?.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_txt_black_deep
                            )
                        )


                        binding_list?.ayatEn?.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_txt_black_deep
                            )
                        )
                        binding_list?.transliteration?.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_txt_black_deep
                            )
                        )


                        if (isMiniPlayerCall) {
                            playQuranAudio(absoluteAdapterPosition)
                            isMiniPlayerCall = false
                        }

                    } else {
                        //playLoadingState(false)
                        binding_list?.btnPlay?.show()
                        binding_list?.playLoading?.hide()
                        binding_list?.btnPlay?.setImageDrawable(
                            AppCompatResources.getDrawable(
                                binding_list.btnPlay.context,
                                R.drawable.ic_quran_play_fill
                            )
                        )

                        binding_list?.quranCard?.setCardBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_white
                            )
                        )
                        binding_list?.quranCard?.strokeWidth = 0.dp
                        binding_list?.ayatArabic?.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_txt_black_deep
                            )
                        )

                        binding_list?.ayatBn?.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_txt_ash
                            )
                        )

                        binding_list?.ayatEn?.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_txt_ash
                            )
                        )
                        binding_list?.transliteration?.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_txt_ash
                            )
                        )

                    }

                    // favorite
                    if(getAyatData.IsFavorite)
                        binding_list?.btnFav?.setImageDrawable(
                            AppCompatResources.getDrawable(
                                binding_list.btnPlay.context,
                                R.drawable.deen_ic_bookmark_active
                            )
                        )
                    else
                        binding_list?.btnFav?.setImageDrawable(
                            AppCompatResources.getDrawable(
                                binding_list.btnPlay.context,
                                R.drawable.deen_ic_bookmark
                            )
                        )

                    binding_list?.btnFav?.setOnClickListener {
                        callback.ayatFavClicked(data[absoluteAdapterPosition],absoluteAdapterPosition)
                    }

                    binding_list?.btnTafseer?.setOnClickListener {
                        callback.tafsirBtnClicked(getAyatData.SurahId,getAyatData.VerseId,ayatArabic,arabicFont)
                    }


                    binding_list?.zoomBtn?.setOnClickListener {
                        callback.zoomBtnClickedADP()
                    }

                    binding_list?.btnMore?.setOnClickListener {
                        callback.option3dotClicked(getAyatData)
                    }

                    binding_list?.root?.setOnClickListener {
                        callback.option3dotClicked(getAyatData)
                    }

                    /*binding_list?.btnShare?.setOnClickListener {
                        callback.customShareAyat(
                            enText = binding_list.ayatEn.text.toString(),
                            bnText = binding_list.ayatBn.text.toString(),
                            arText = binding_list.ayatArabic.text.toString(),
                            verseKey = getAyatData.VerseKey
                        )
                    }*/

                }

                READING_MODE -> {

                    if(!isBnReading) {

                        when(arabicFont){

                            1-> {
                                val customFont =
                                    ResourcesCompat.getFont(itemView.context, R.font.indopakv2)
                                binding_read?.ayat?.typeface = customFont
                            }

                           /* 2-> {
                                val customFont =
                                    ResourcesCompat.getFont(itemView.context, R.font.kfgqpc_font)
                                binding_read?.ayat?.typeface = customFont
                            }

                            3-> {
                                val customFont =
                                    ResourcesCompat.getFont(itemView.context, R.font.al_majed_quranic_font_regular)
                                binding_read?.ayat?.typeface = customFont
                            }*/
                        }

                    }
                    else
                        binding_read?.ayat?.typeface = Typeface.DEFAULT


                    if(data.isNotEmpty()) {
                        // String builder

                        var startIndexForActive = 0

                        val stringBuilder = StringBuilder()

                        data.forEach {

                            val ayatArabic =
                                if(isBnReading) "${it.Transliteration_bn.trim()} "
                                else
                                {
                                    when (arabicFont) {
                                        1 -> " ${it.Arabic_indopak}"
                                        2 -> " ${it.Arabic_uthmani}"
                                        else -> " ${it.Arabic_Custom}"
                                    }
                                }




                            stringBuilder.append(ayatArabic)
                        }


                        val verseText = stringBuilder.toString()
                        val spannableString = SpannableString(verseText)

                        data.forEach {

                            // detect verse click
                            val clickableSpan = object : ClickableSpan() {
                                override fun onClick(view: View) {
                                    // itemView.context.toast(it.verse_number.toString())
                                    quranPlayBtnClick(it.VerseId-1)
                                    // Handle verse click event
                                }
                                override fun updateDrawState(ds: TextPaint) {
                                    // Remove underline
                                    ds.isUnderlineText = false
                                    if(isPlaying && previousCallbackPosition == it.VerseId-1) {
                                        //playLoadingState(false)
                                        ds.color = ContextCompat.getColor(
                                            itemView.context,
                                            R.color.deen_primary
                                        )
                                    }
                                    else
                                        ds.color = ContextCompat.getColor(
                                            itemView.context,
                                            R.color.deen_txt_black_deep
                                        )

                                }
                            }

                            val ayatArabic =
                                if(isBnReading) "${it.Transliteration_bn.trim()} "
                                else
                                {
                                    when (arabicFont) {
                                        1 -> " ${it.Arabic_indopak}"
                                        2 -> " ${it.Arabic_uthmani}"
                                        else -> " ${it.Arabic_Custom}"
                                    }
                                }


                            val start = verseText.indexOf(ayatArabic)
                            val end = start + ayatArabic.length

                            if(isPlaying && previousCallbackPosition == it.VerseId-1 ) {
                                startIndexForActive = start
                                playLoadingState(false)
                            }

                            spannableString.setSpan(
                                clickableSpan,
                                start,
                                end,
                                SpannableString.SPAN_INCLUSIVE_INCLUSIVE
                            )

                            binding_read?.ayat?.apply {
                                text = spannableString
                                movementMethod = LinkMovementMethod.getInstance()
                            }
                        }


                        CoroutineScope(Dispatchers.Main).launch {
                            binding_read?.ayat?.apply {
                                if (startIndexForActive > 0)
                                    targetSpannableOffset = this.layout.getLineTop(
                                        this.layout.getLineForOffset(startIndexForActive)
                                    )
                                setTextSize(TypedValue.COMPLEX_UNIT_SP, theme_font_size)
                            }
                        }

                        if (isMiniPlayerCall) {
                            playQuranAudio(previousCallbackPosition)
                            isMiniPlayerCall = false
                        }

                    }

                }

            }

        }

        private fun playLoadingState(bol:Boolean)
        {
            binding_list?.btnPlay?.invisible(bol)
            binding_list?.playLoading?.visible(bol)
            callback.isLoadingState(bol)
        }

        private fun playQuranAudio(pos: Int)
        {
            if(pos<0)
                return

            playLoadingState(true)

            callback.startPlayingQuran(data,pos)

            /* CoroutineScope(Dispatchers.IO).launch {

                 AudioManager().getInstance().playAudioFromUrl(getQuranAudioUrl("${BASE_CONTENT_URL_SGP}${data[pos].AudioUrl}",getQariWiseFolder()),pos)
             }*/
        }

        private fun quranPlayBtnClick(pos:Int)
        {
            if (previousCallbackPosition != pos) {
                isMediaPause(previousCallbackPosition)
                previousCallbackPosition = pos

                playQuranAudio(pos)

            } else if (!isPlaying)
                playQuranAudio(pos)
            else {
                pauseQuranAudio(pos)

            }
        }

        private fun pauseQuranAudio(pos:Int)
        {
            playLoadingState(false)
            /* CoroutineScope(Dispatchers.IO).launch {
                // AudioManager().getInstance().pauseMediaPlayer(pos)

                 withContext(Dispatchers.Main)
                 {
                     callback.isAyatPause()
                 }
             }*/
            isMediaPause(pos,false)
        }

        override fun isPlaying(position: Int, duration: Long?, surahID: Int) {

            playLoadingState(false)

            if(this@AlQuranAyatAdapter.surahID != surahID)
                return

            CoroutineScope(Dispatchers.Main).launch {
                isMediaPlaying(position)
                callback.isAyatPlaying(position, duration)
            }
        }

        override fun isPause(position: Int,byService:Boolean) {
            isMediaPause(position,byService)
        }


        override fun isStop(position: Int) {
            /*CoroutineScope(Dispatchers.Main).launch {
                isMediaPause(position)
                callback.isAyatPause()
            }*/
        }

        override fun isComplete(position: Int, surahID: Int) {
            playLoadingState(false)

            if(this@AlQuranAyatAdapter.surahID != surahID)
                return

            if(auto_play_next) {
                if (position + 1 < data.size) {

                    if(previousCallbackPosition>=0)
                        isMediaPause(previousCallbackPosition,true)

                    if (previousCallbackPosition != position + 1)
                        previousCallbackPosition = position + 1

                    miniPlayerCall(byService = true)

                    /*CoroutineScope(Dispatchers.IO).launch {
                        AudioManager().getInstance().playAudioFromUrl(getQuranAudioUrl(
                            "${BASE_CONTENT_URL_SGP}${data[position+1].AudioUrl}",getQariWiseFolder()),
                            position + 1
                        )
                    }*/



                } else {
                    if(!auto_play_next)
                        previousCallbackPosition = 0
                    //callback.isAyatPause()
                    isMediaPause(position,true)
                }

                callback.playNextAyat(position + 1)
            }
            else
            {
                //callback.isAyatPause()
                isMediaPause(position,true)
            }
        }
    }

}
