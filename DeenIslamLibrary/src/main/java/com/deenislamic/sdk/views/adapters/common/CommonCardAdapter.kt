package com.deenislamic.sdk.views.adapters.common;

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.CommonCardCallback
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.utils.visible
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

internal class CommonCardAdapter(
    private val cardWidthDp: Int = 0,
    private val isShowPlayIcon: Boolean = false,
    private val isShowProgress: Boolean = false,
    private val isShowLiveIcon:Boolean = false,
    private val data:ArrayList<CommonCardData>,
    private val itemMarginTop:Int = -1,
    private val itemMarginLeft:Int = -1,
    private val itemMarginRight:Int = -1,
    private val itemMarginBottom:Int = -1,
    private val itemPaddingBottom:Int = -1,
    private val bannerSize:Int = 0

) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<CommonCardCallback>()
    private var lastUpdatedItemID = -1
    private var lastUpdatedData: CommonCardData? = null
    private var isListView = false

    private val deviceWidth: Int by lazy {
        val displayMetrics = this@CommonCardAdapter.deviceContext?.resources?.displayMetrics
        displayMetrics?.widthPixels ?: 0
    }

    private var deviceContext: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val holder =  ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quranic_v2, parent, false)
        )

        deviceContext = holder.itemView.context

        return holder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun updateView(isListView: Boolean) {
        this.isListView = isListView
        //notifyItemRangeChanged(0,itemCount)
    }

    fun updatePlayPauseIcon(newdata: CommonCardData)
    {

        val oldList = data.toList()

        val matchingVideoDataIndex = oldList.indexOfFirst { it.Id == newdata.Id }

        if(lastUpdatedItemID!=-1 && lastUpdatedData!=null)
            data[lastUpdatedItemID] = lastUpdatedData!!

        Log.e("updatePlayPauseIcon",lastUpdatedItemID.toString())


        if(matchingVideoDataIndex!=-1) {
            data[matchingVideoDataIndex] = newdata
            lastUpdatedItemID = matchingVideoDataIndex
            lastUpdatedData = oldList[matchingVideoDataIndex].copy()
        }


        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                oldList,
                data
            )
        )

        diffResult.dispatchUpdatesTo(this)
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val banner:AppCompatImageView by lazy { itemView.findViewById(R.id.banner) }
        private val progress:LinearProgressIndicator by lazy { itemView.findViewById(R.id.progress) }
        private val icPlay:AppCompatImageView by lazy { itemView.findViewById(R.id.icPlay) }
        private val icLive:AppCompatImageView by lazy { itemView.findViewById(R.id.icLive) }
        private val textContent:AppCompatTextView by lazy { itemView.findViewById(R.id.textContent) }
        private val subContent:AppCompatTextView by lazy { itemView.findViewById(R.id.subContent) }
        private val mainBtn:MaterialButton by lazy { itemView.findViewById(R.id.mainBtn) }
        private val container:ConstraintLayout by lazy { itemView.findViewById(R.id.container) }

        private val itemGridView: MaterialCardView by lazy {  itemView.findViewById(R.id.ItemGridView) }
        private val itemListView: MaterialCardView by lazy { itemView.findViewById(R.id.ItemListView) }

        private val bannerList: AppCompatImageView by lazy { itemView.findViewById(R.id.bannerList) }
        private val textContentList: AppCompatTextView by lazy { itemView.findViewById(R.id.textContentList)}
        private val subContentList: AppCompatTextView by lazy { itemView.findViewById(R.id.subContentList) }
        private val ic_play_oval_List: AppCompatImageView by lazy { itemView.findViewById(R.id.ic_play_oval_List) }
        private val icCompleted:AppCompatImageView = itemView.findViewById(R.id.icCompleted)


        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = data[absoluteAdapterPosition]


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
                            R.drawable.deen_ic_play_oval
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
                            R.drawable.deen_ic_play_oval
                        )
                    )
            }

            icCompleted.visible(getData.IsCompleted)


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

            if(isShowLiveIcon || getData.isLive)
                icLive.show()
            else
                icLive.hide()



            // Load banner image
            banner.imageLoad(
                placeholder_16_9 = true,
                url = BASE_CONTENT_URL_SGP+getData.imageurl,
                size = bannerSize
            )
            bannerList.imageLoad(
                placeholder_1_1 = true,
                url = BASE_CONTENT_URL_SGP+getData.imageurl,
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

            if(absoluteAdapterPosition==itemCount-1)
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 0


            if(itemMarginBottom>=0)
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = itemMarginBottom


            // Item Click Listner

            itemView.setOnClickListener {
                callback?.commonCardClicked(getData,absoluteAdapterPosition)
            }
        }
    }

    internal class DataDiffCallback(
        private val oldList: List<CommonCardData>,
        private val newList: List<CommonCardData>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].Id == newList[newItemPosition].Id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}