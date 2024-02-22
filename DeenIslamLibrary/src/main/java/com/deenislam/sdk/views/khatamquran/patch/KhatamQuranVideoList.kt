package com.deenislam.sdk.views.khatamquran.patch

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.CommonCardCallback
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.show
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

internal class KhatamQuranVideoList(
    itemView: View,
    pageTitle: String,
    val getData: CommonCardData,
    private val cardWidthDp: Int = 0,
    private val isShowPlayIcon: Boolean = false,
    private val isShowProgress: Boolean = false,
    private val isShowLiveIcon:Boolean = false,
    private val itemMarginTop:Int = -1,
    private val itemMarginLeft:Int = -1,
    private val itemMarginRight:Int = -1,
    private val itemMarginBottom:Int = -1,
    private val itemPaddingBottom:Int = -1,
    private val bannerSize:Int = 0,
    private val absoluteAdapterPosition:Int,
    private val itemCount:Int
) {
    private val banner: AppCompatImageView = itemView.findViewById(R.id.banner)
    private val progress: LinearProgressIndicator = itemView.findViewById(R.id.progress)
    private val icPlay: AppCompatImageView = itemView.findViewById(R.id.icPlay)
    private val icLive: AppCompatImageView = itemView.findViewById(R.id.icLive)
    private val textContent: AppCompatTextView = itemView.findViewById(R.id.textContent)
    private val subContent: AppCompatTextView = itemView.findViewById(R.id.subContent)
    private val mainBtn: MaterialButton = itemView.findViewById(R.id.mainBtn)
    private val container: ConstraintLayout = itemView.findViewById(R.id.container)

    private val itemGridView: MaterialCardView = itemView.findViewById(R.id.ItemGridView)
    private val itemListView: MaterialCardView = itemView.findViewById(R.id.ItemListView)

    private val bannerList: AppCompatImageView = itemView.findViewById(R.id.bannerList)
    private val textContentList: AppCompatTextView = itemView.findViewById(R.id.textContentList)
    private val subContentList: AppCompatTextView = itemView.findViewById(R.id.subContentList)
    private val ic_play_oval_List: AppCompatImageView = itemView.findViewById(R.id.ic_play_oval_List)

    private var lastUpdatedItemID = -1
    private var lastUpdatedData: CommonCardData? = null
    private var isListView = false
    private val callback = CallBackProvider.get<CommonCardCallback>()

    private var deviceContext: Context = itemView.context

    private val deviceWidth: Int by lazy {
        val displayMetrics = this.deviceContext.resources?.displayMetrics
        displayMetrics?.widthPixels ?: 0
    }

    init {

        // grid and listview style
        if (!isListView) {
            itemGridView.show()
            itemListView.hide()
            icPlay.show()
        } else {
            itemGridView.hide()
            itemListView.show()
            icPlay.hide()
        }

        // Have play icon

        if(isShowPlayIcon)
            icPlay.show()


        // play pause icon sync with player

        if(!isListView) {

            if (getData.isPlaying)
                icPlay.setImageDrawable(
                    ContextCompat.getDrawable(
                        icPlay.context,
                        R.drawable.deen_ic_pause_overlay
                    )
                )
            else
                icPlay.setImageDrawable(
                    ContextCompat.getDrawable(
                        icPlay.context,
                        R.drawable.ic_play_oval
                    )
                )
        }
        else
        {
            if (getData.isPlaying)
                ic_play_oval_List.setImageDrawable(
                    ContextCompat.getDrawable(
                        ic_play_oval_List.context,
                        R.drawable.deen_ic_pause_overlay
                    )
                )
            else
                ic_play_oval_List.setImageDrawable(
                    ContextCompat.getDrawable(
                        ic_play_oval_List.context,
                        R.drawable.ic_play_oval
                    )
                )
        }


        // Have progress bar

        if(isShowProgress) {
            progress.show()
            if ((getData.DurationInSec) > 0) {
                val percentageProgress: Double = (getData.DurationWatched).toDouble() / (getData.DurationInSec).toDouble()
                val totalProgress : Double = percentageProgress*100
                progress.progress = totalProgress.toInt()
            } else {
                progress.progress = 0
            }

        }

        // setup card width

        if(cardWidthDp>0 && itemCount>1) {

            val totalMinWidthSpace = itemCount * cardWidthDp
            val remainingSpace = deviceWidth - totalMinWidthSpace
            val additionalWidthPerItem = if (remainingSpace > 0) remainingSpace / itemCount else 0
            val itemWidth = cardWidthDp + additionalWidthPerItem

            itemView.layoutParams.width = itemWidth
        }

        // button text

        if(getData.buttonTxt==null)
            mainBtn.hide()
        else
            mainBtn.show()
        mainBtn.text = getData.buttonTxt


        // have any live icon

        if(isShowLiveIcon)
            icLive.show()



        // Load banner image
        banner.imageLoad(
            placeholder_16_9 = true,
            url = BASE_CONTENT_URL_SGP +getData.imageurl,
            size = bannerSize
        )
        bannerList.imageLoad(
            placeholder_1_1 = true,
            url = BASE_CONTENT_URL_SGP +getData.imageurl,
            size = bannerSize
        )


        // set Text Content / Title

        if(getData.title == null) {
            textContent.hide()
            textContentList.hide()
        }
        else {
            textContent.show()
            textContentList.show()
            textContent.text = getData.title
            textContentList.text = getData.title
        }

        // set Sub Content

        if(getData.reference == null) {
            subContent.hide()
            subContentList.hide()
        }
        else {
            subContent.show()
            subContentList.show()
            subContentList.text = getData.reference
            subContent.text = getData.reference
        }

        // Set Padding / Margin

        if(itemPaddingBottom>=0)
            container.setPadding(0,0,0,itemPaddingBottom)


        if(itemMarginTop>=0)
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = itemMarginTop

        if(itemMarginLeft>=0)
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = itemMarginLeft

        if(itemMarginRight>=0)
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = itemMarginRight

       /* if(absoluteAdapterPosition==itemCount-1)
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 0*/


        if(itemMarginBottom>=0)
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = itemMarginBottom


        // Item Click Listner

        itemView.setOnClickListener {
            callback?.commonCardClicked(getData,absoluteAdapterPosition)
        }
    }
}