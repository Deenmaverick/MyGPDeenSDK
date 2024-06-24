package com.deenislamic.sdk.views.adapters.podcast

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.DashboardPatchCallback
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator

internal class LivePodcastRecentAdapter(private val items: List<Item>,
                                        private val isShowPlayIcon: Boolean = false) : RecyclerView.Adapter<BaseViewHolder>() {

    private var callback = CallBackProvider.get<HorizontalCardListCallback>()
    private var dashbCallback = CallBackProvider.get<DashboardPatchCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quranic_v2, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val banner:AppCompatImageView by lazy { itemView.findViewById(R.id.banner) }
        private val textContent:AppCompatTextView by lazy { itemView.findViewById(R.id.textContent) }
        private val subContent:AppCompatTextView by lazy { itemView.findViewById(R.id.subContent) }
        private val midContent:AppCompatTextView by lazy { itemView.findViewById(R.id.midContent) }
        private val progress:LinearProgressIndicator by lazy { itemView.findViewById(R.id.progress) }
        private val mainBtn:MaterialButton by lazy { itemView.findViewById(R.id.mainBtn) }
        private val icPlay:AppCompatImageView by lazy { itemView.findViewById(R.id.icPlay) }
        private val icLive:AppCompatImageView by lazy { itemView.findViewById(R.id.icLive) }
        private val footer: ConstraintLayout by lazy { itemView.findViewById(R.id.footer) }

        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = items[position]

            icPlay.visible(isShowPlayIcon || getData.isVideo)
            icLive.visible(getData.isLive)
            //itemView.layoutParams.width = 264.dp
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 0.dp
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 8.dp
            banner.show()
            banner.imageLoad(
                url = getData.contentBaseUrl+"/"+getData.imageurl1,
                placeholder_16_9 = (getData.FeatureSize == "16x9" || getData.FeatureSize.toString().isEmpty()),
                placeholder_1_1  = getData.FeatureSize == "1x1"
            )
            progress.hide()
            textContent.text = getData.itemTitle
            midContent.text = getData.itemMidContent
            subContent.text = getData.itemSubTitle

            textContent.maxLines = 1
            textContent.ellipsize = TextUtils.TruncateAt.END


            if(getData.itemTitle?.isEmpty() == true) {
                textContent.hide()
                footer.hide()
            }

            if(getData.itemSubTitle.toString().isEmpty())
                subContent.hide()

            if(getData.itemMidContent.toString().isEmpty())
                midContent.hide()

            if(getData.itemBtnText.toString().isNotEmpty())
            mainBtn.text = getData.itemBtnText
            else
                mainBtn.hide()

            if(!mainBtn.isVisible && subContent.isVisible)
                subContent.setPadding(0,2.dp,0,10.dp)

            else if(!subContent.isVisible && !mainBtn.isVisible && midContent.isVisible)
                midContent.setPadding(0,2.dp,0,10.dp)

            else if(!subContent.isVisible && !mainBtn.isVisible && !midContent.isVisible && textContent.isVisible)
                textContent.setPadding(0,2.dp,0,10.dp)

            itemView.setOnClickListener {
                callback = CallBackProvider.get<HorizontalCardListCallback>()
                dashbCallback = CallBackProvider.get<DashboardPatchCallback>()
                callback?.patchItemClicked(getData)
                dashbCallback?.dashboardPatchClickd(getData.ContentType,getData)
            }
        }
    }
}