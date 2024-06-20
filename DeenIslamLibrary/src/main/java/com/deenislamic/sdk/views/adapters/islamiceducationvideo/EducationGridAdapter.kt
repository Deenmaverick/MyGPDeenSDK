package com.deenislamic.sdk.views.adapters.islamiceducationvideo;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamicEducationCallback
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class EducationGridAdapter(
    private val namercList: RecyclerView,
    private val educationVideos: List<CommonCardData>
) : RecyclerView.Adapter<BaseViewHolder>() {
private val callback = CallBackProvider.get<IslamicEducationCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_islamic_education_video, parent, false)
        )


    override fun getItemCount(): Int = educationVideos.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val tvStoryName: AppCompatTextView = itemView.findViewById(R.id.tvStoryName)
        private val ivStory: AppCompatImageView = itemView.findViewById(R.id.ivStory)
        override fun onBind(position: Int) {
            super.onBind(position)

            val listItem = educationVideos.get(position)
            tvStoryName.text = listItem.title
            ivStory.imageLoad(BASE_CONTENT_URL_SGP + listItem.imageurl, size = 400)

            itemView.setOnClickListener {

                callback?.videoItemClick(position)
            }

           /* val isLastItem = position == itemCount - 1
            val isTotalCountOdd = itemCount % 2 != 0

            if (isLastItem && isTotalCountOdd) {
                // Assuming you're using a LinearLayout or similar as root for your item layout
                val layoutParams = itemView.layoutParams as GridLayoutManager.LayoutParams

                // Set width to half the RecyclerView width (or close to half, depending on margins, paddings, etc.)
                layoutParams.width = namercList.width / 2

                // Use margins to center the view (subtract any margins/paddings if needed)
                layoutParams.leftMargin = namercList.width / 4
                layoutParams.rightMargin = namercList.width / 4

                itemView.layoutParams = layoutParams
            } else {
                // Reset to original layout parameters if they've been changed
                val layoutParams = itemView.layoutParams as GridLayoutManager.LayoutParams
                layoutParams.width = GridLayoutManager.LayoutParams.MATCH_PARENT
                layoutParams.leftMargin = 8.dp
                layoutParams.rightMargin = 8.dp
                itemView.layoutParams = layoutParams
            }*/
        }
    }
}