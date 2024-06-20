package com.deenislamic.sdk.views.common.patch;

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.CommonCardCallback
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.show
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

internal class SingleCardItemPatch(
    private val view: View,
    private val title: String="",
    private val isLive:Boolean = false,
    private val isPlayBtn:Boolean = false,
    private val isFooter:Boolean = false,
    private val textContentData:String = "Live Quran Class",
    private val subContentData:String ="Sheikh Ahmadullah • 01 Oct 2023, 10:00 PM",
    private val mainBtnText:String ="Join Class",
    private val viewMarginTop:Int = 16.dp,
    private val cardMarginTop:Int  = 16.dp,
    private val bannerImg:String = ""
) {

    private val itemTitle:AppCompatTextView = view.findViewById(R.id.itemTitle)
    private val banner: AppCompatImageView = view.findViewById(R.id.banner)
    private val textContent:AppCompatTextView = view.findViewById(R.id.textContent)
    private val subContent:AppCompatTextView = view.findViewById(R.id.subContent)
    private val mainBtn: MaterialButton = view.findViewById(R.id.mainBtn)
    private val callback = CallBackProvider.get<CommonCardCallback>()
    private val icPlay:AppCompatImageView = view.findViewById(R.id.icPlay)
    private val icLive:AppCompatImageView = view.findViewById(R.id.icLive)
    private val itemGridView:MaterialCardView = view.findViewById(R.id.ItemGridView)
    private val footer:ConstraintLayout = view.findViewById(R.id.footer)



    fun load()
    {

        (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin = viewMarginTop
        (itemGridView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = cardMarginTop

        // set view
        if(title.isNotEmpty()) {
            itemTitle.text = title
            itemTitle.show()
        }

        if(isPlayBtn)
        icPlay.show()

        if(isLive)
        icLive.show()

        banner.imageLoad(url = bannerImg, placeholder_16_9 =true)

        if(!isFooter)
            footer.hide()
        else {

            textContent.text = textContentData
            subContent.text = subContentData
            mainBtn.text = mainBtnText
        }

        view.setOnClickListener {
            callback?.singleCardItemClicked()
        }

    }
}