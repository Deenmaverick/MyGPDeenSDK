package com.deenislam.sdk.views.adapters.quran;

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.databinding.ItemQuranAyatBinding
import com.deenislam.sdk.service.libs.media3.AudioPlayerCallback
import com.deenislam.sdk.service.network.response.quran.surah_details.Ayath
import com.deenislam.sdk.views.base.BaseViewHolder


class AlQuranAyatAdapter(
    private val audioPlayerCallback: AudioPlayerCallback
) :  RecyclerView.Adapter<BaseViewHolder>() {

    private var data: ArrayList<Ayath> = arrayListOf()
    private var previousCallbackPosition:Int = -1
    private var isPlaying:Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemBinding = ItemQuranAyatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            itemBinding
        )
    }

    fun isPlaying(position: Int)
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

    fun isPause(position: Int)
    {

        isPlaying = false
        if(previousCallbackPosition>=0 && previousCallbackPosition!=position) {
            notifyItemChanged(previousCallbackPosition)
        }
        if(position>=0)
            notifyItemChanged(position)

    }


    fun update(surahData:ArrayList<Ayath>)
    {
        data.addAll(surahData)
        notifyItemInserted(data.size - 1)
    }
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(private val binding: ItemQuranAyatBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            super.onBind(position)
            binding.ayatArabic.text = data[position].TextInArabic
            binding.ayatBn.text = data[position].Text
            binding.surayAyat.text = "${data[position].SurahNo}:${data[position].AyatOrder}"

            binding.btnPlay.setOnClickListener {

                Log.e("isPlaying",isPlaying.toString())

                if(previousCallbackPosition != position) {
                    audioPlayerCallback.pause(position)
                    previousCallbackPosition = position
                    audioPlayerCallback.playAudioFromUrl(
                        "${data[position].ContentBaseUrl}/${data[position].ContentUrl}",
                        position
                    )
                }
                else if(previousCallbackPosition == position && !isPlaying)
                {
                    audioPlayerCallback.pause(position)
                    audioPlayerCallback.playAudioFromUrl(
                        "${data[position].ContentBaseUrl}/${data[position].ContentUrl}",
                        position
                    )
                }
                else
                    audioPlayerCallback.pause(position)

            }

            if(isPlaying && previousCallbackPosition==position)
                binding.btnPlay.setImageDrawable(AppCompatResources.getDrawable(binding.btnPlay.context, R.drawable.ic_pause_fill))
            else
                binding.btnPlay.setImageDrawable(AppCompatResources.getDrawable(binding.btnPlay.context, R.drawable.ic_quran_play_fill))

        }

    }


}
