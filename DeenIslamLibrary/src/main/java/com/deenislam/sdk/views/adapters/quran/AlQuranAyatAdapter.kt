package com.deenislam.sdk.views.adapters.quran;

import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.databinding.ItemQuranAyatBinding
import com.deenislam.sdk.databinding.ItemQuranReadingBinding
import com.deenislam.sdk.service.callback.AlQuranAyatCallback
import com.deenislam.sdk.service.libs.media3.APAdapterCallback
import com.deenislam.sdk.service.libs.media3.AudioManager
import com.deenislam.sdk.utils.BASE_QURAN_VERSE_AUDIO_URL
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.base.BaseViewHolderBinding
import com.deenislam.sdk.service.network.response.quran.verses.Verse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val LIST_MODE = 0
private const val READING_MODE = 1
internal class AlQuranAyatAdapter(
    private val callback: AlQuranAyatCallback,
    private val isReadingMode: Boolean = false
) :  RecyclerView.Adapter<BaseViewHolderBinding>() {

    private var data: ArrayList<Verse> = arrayListOf()
    private var previousCallbackPosition:Int = -1
    private var isPlaying:Boolean = false
    private var isMiniPlayerCall:Boolean = false
    private var targetSpannableOffset: Int = 0

    // player setting
    private var theme_font_size:Float = 24F
    private var translation_font_size:Float = 14F
    private var setting_transliteration = true
    private var auto_play_next = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolderBinding =

        when(viewType)
        {
            READING_MODE ->  ViewHolder(
                binding_read = ItemQuranReadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            LIST_MODE ->  ViewHolder(
                binding_list = ItemQuranAyatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }



    fun clear()
    {
        data = arrayListOf()
        previousCallbackPosition = -1
        isPlaying = false
        notifyDataSetChanged()
    }

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

    fun isMediaPause(position: Int)
    {
        isPlaying = false
        AudioManager().getInstance().releasePlayer()
        if(previousCallbackPosition>=0 && previousCallbackPosition!=position) {
            notifyItemChanged(previousCallbackPosition)
            //notifyDataSetChanged()
        }
        if(position>=0)
        //notifyDataSetChanged()
            notifyItemChanged(position)

    }

    fun miniPlayerCall()
    {

        if(isPlaying) {
            AudioManager().getInstance().releasePlayer()
            callback.isAyatPause()
            isPlaying = false

        }
        else
        {
            if(previousCallbackPosition<0)
                previousCallbackPosition = 0

            isPlaying = true
            isMiniPlayerCall = true
        }

        if(previousCallbackPosition>=0)
            notifyItemChanged(previousCallbackPosition)
    }

    fun miniPlayerPrevCall()
    {
        if(previousCallbackPosition>=0)
        {

            // change already playing item
            isPlaying = false
            isMiniPlayerCall = true

            notifyItemChanged(previousCallbackPosition)

            previousCallbackPosition--
            AudioManager().getInstance().releasePlayer()
            callback.isAyatPause()

            isPlaying = true
            //isMiniPlayerCall = true

            //notifyDataSetChanged()
            notifyItemChanged(previousCallbackPosition)
        }

    }

    fun miniPlayerNextCall()
    {

        if(previousCallbackPosition+1<data.size) {

            isPlaying = false
            isMiniPlayerCall = true
            notifyItemChanged(previousCallbackPosition)

            previousCallbackPosition++
            AudioManager().getInstance().releasePlayer()
            callback.isAyatPause()

            isPlaying = true

            notifyItemChanged(previousCallbackPosition)

            //notifyDataSetChanged()
        }

    }


    fun update(surahData:ArrayList<Verse>)
    {
        data.addAll(surahData)
        if(!isReadingMode)
        // notifyItemRangeInserted(data.size - surahData.size, surahData.size)
            notifyItemInserted(itemCount)
        else
            notifyItemRangeChanged(0,1)
    }

    fun update_theme_font_size(fontsize: Float)
    {
        theme_font_size = fontsize
        //notifyDataSetChanged()
    }

    fun update_translation_font_size(fontsize: Float)
    {
        translation_font_size = fontsize
        //notifyDataSetChanged()
    }

    fun update_transliteration(bol:Boolean)
    {
        setting_transliteration = bol
        //notifyDataSetChanged()
    }

    fun update_auto_play_next(bol:Boolean)
    {
        auto_play_next = bol
        //notifyDataSetChanged()
    }

    fun getDataSize() = data.size

    fun getTargetIndexOffset() = targetSpannableOffset

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

    inner class ViewHolder(
        private val binding_list: ItemQuranAyatBinding? = null,
        private val binding_read: ItemQuranReadingBinding? = null
    ) : BaseViewHolderBinding(
        if(binding_list!=null) binding_list.root
        else if(binding_read !=null) binding_read.root
        else null
    ),
        APAdapterCallback {
        init {
            AudioManager().getInstance().setupAdapterResponseCallback(this@ViewHolder)
        }
        override fun onBind(position: Int , viewtype: Int) {
            super.onBind(position)

            when(viewtype) {
                LIST_MODE -> {
                    var ayatArabic = ""
                    var wordToHighlight = ""
                    var transliteration = ""

                    data[position].words.forEach {
                        ayatArabic += " " + it.text
                        //wordToHighlight = it.text
                        if (!it.transliteration.text.isNullOrEmpty()) {
                            transliteration +=  it.transliteration.text +" "
                        }
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

                    binding_list?.ayatArabic?.text = ayatArabic
                    binding_list?.ayatArabic?.setTextSize(TypedValue.COMPLEX_UNIT_SP,theme_font_size)
                    binding_list?.transliteration?.text = transliteration

                    binding_list?.ayatEn?.setTextSize(TypedValue.COMPLEX_UNIT_SP,translation_font_size)
                    binding_list?.ayatBn?.setTextSize(TypedValue.COMPLEX_UNIT_SP,translation_font_size)


                    if(data[position].translations.isNotEmpty()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding_list?.ayatEn?.text = Html.fromHtml(
                                data[position].translations[0].text,
                                Html.FROM_HTML_MODE_LEGACY
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            binding_list?.ayatEn?.text = Html.fromHtml(
                                data[position].translations[0].text
                            )
                        }

                    }
                    else
                        binding_list?.ayatEn?.hide()

                    if(data[position].translations.size>1) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding_list?.ayatBn?.text = Html.fromHtml(
                                data[position].translations[1].text,
                                Html.FROM_HTML_MODE_LEGACY
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            binding_list?.ayatBn?.text = Html.fromHtml(
                                data[position].translations[1].text
                            )
                        }

                    }
                    else
                        binding_list?.ayatBn?.hide()



                    binding_list?.surayAyat?.text = data[position].verse_key

                    binding_list?.btnPlay?.setOnClickListener {
                        quranPlayBtnClick(position)
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
                                R.color.deen_card_bg
                            )
                        )
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

                        if (isMiniPlayerCall) {
                            playQuranAudio(position)
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

                    }

                }

                READING_MODE -> {


                    // String builder

                    var startIndexForActive = 0

                    val stringBuilder = StringBuilder()
                    data.forEach {

                        var ayatArabic = ""

                        it.words.forEach {
                            ayatArabic += " " + it.text
                            //wordToHighlight = it.text
                        }

                        stringBuilder.append("${ayatArabic}")
                    }

                    val verseText = stringBuilder.toString()
                    val spannableString = SpannableString(verseText)

                    for ((index, data) in data.withIndex()) {

                        val it = data

                        // detect verse click
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(view: View) {
                                // itemView.context.toast(it.verse_number.toString())
                                quranPlayBtnClick(index)
                                // Handle verse click event
                            }
                            override fun updateDrawState(ds: TextPaint) {
                                // Remove underline
                                ds.isUnderlineText = false
                                if(isPlaying && previousCallbackPosition == index) {
                                    playLoadingState(false)
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


                        var ayatArabic = ""

                        it.words.forEach {
                            ayatArabic += " " + it.text
                            //wordToHighlight = it.text
                        }


                        val start = verseText.indexOf(ayatArabic)
                        val end = start + ayatArabic.length

                        //it.verse_number-1
                        if(isPlaying && previousCallbackPosition == index )
                            startIndexForActive = start

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
                            if(startIndexForActive>0)
                                targetSpannableOffset = this.layout.getLineTop(this.layout.getLineForOffset(startIndexForActive))
                            setTextSize(TypedValue.COMPLEX_UNIT_SP,theme_font_size)
                        }
                    }



                    if (isMiniPlayerCall) {
                        playQuranAudio(position)
                        isMiniPlayerCall = false
                    }


                }

            }

        }

        private fun playLoadingState(bol:Boolean)
        {
            binding_list?.btnPlay?.visible(!bol)
            binding_list?.playLoading?.visible(bol)
            callback.isLoadingState(bol)
        }

        private fun playQuranAudio(pos: Int)
        {
                playLoadingState(true)

                CoroutineScope(Dispatchers.IO).launch {

                    AudioManager().getInstance().playAudioFromUrl(
                        "${BASE_QURAN_VERSE_AUDIO_URL}${data[pos].audio.url}",
                        pos
                    )
                }
        }

        private fun quranPlayBtnClick(pos:Int)
        {
            if (previousCallbackPosition != pos) {
                //isMediaPause(position)
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
            CoroutineScope(Dispatchers.IO).launch {

                AudioManager().getInstance().pauseMediaPlayer(pos)

                withContext(Dispatchers.Main)
                {
                    callback.isAyatPause()
                }
            }
        }

        override fun isPlaying(position: Int, duration: Int?) {
            CoroutineScope(Dispatchers.Main).launch {
                isMediaPlaying(position)
                callback.isAyatPlaying(position,duration)
            }
        }

        override fun isPause(position: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                isMediaPause(position)
            }
        }

        override fun isStop(position: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                isMediaPause(position)
                callback.isAyatPause()
            }
        }

        override fun isComplete(position: Int) {

            if(auto_play_next) {
                if (position + 1 < data.size) {
                    CoroutineScope(Dispatchers.IO).launch {
                        AudioManager().getInstance().playAudioFromUrl(
                            "${BASE_QURAN_VERSE_AUDIO_URL}${data[position + 1].audio.url}",
                            position + 1
                        )
                    }
                    if (previousCallbackPosition != position + 1)
                        previousCallbackPosition = position + 1
                } else {
                    previousCallbackPosition = 0
                    callback.isAyatPause()
                    isMediaPause(position)
                }

                callback.playNextAyat(position + 1)
            }
            else
            {
                callback.isAyatPause()
                isMediaPause(position)
            }
        }
    }
}
