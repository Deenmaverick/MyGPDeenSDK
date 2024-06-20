package com.deenislamic.sdk.views.adapters.khatamquran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.khatamquran.patch.KhatamQuranRecentVideo
import com.deenislamic.sdk.views.khatamquran.patch.KhatamQuranVideoList

private const val RECENT_WATCH_VIDEO_KHATAM_QURAN = 0
private const val EDUCATIONAL_VIDEO_KHATAM_QURAN = 1
internal class KhatamQuranVideoHomeAdapter(
    private val recentlyWatchedVideos: List<CommonCardData>,
    private val khatamQuranVideos: List<CommonCardData>
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var videoPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

       /* val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_v2, parent, false)

        val rootview: AsyncViewStub = main_view.findViewById(R.id.widget)

        when (viewType) {
            RECENT_WATCH_VIDEO_KHATAM_QURAN,
             -> {
                prepareStubView<View>(
                    rootview.findViewById(R.id.widget),
                    R.layout.layout_horizontal_listview_v2
                ){

                    onBindViewHolder(ViewHolder(main_view, true), viewType)

                }
            }

            EDUCATIONAL_VIDEO_KHATAM_QURAN -> {
                prepareStubView<View>(
                    rootview.findViewById(R.id.widget),
                    R.layout.item_quranic_v2
                ){

                    onBindViewHolder(ViewHolder(main_view, true), viewType)

                }
            }
        }*/

        val layout =  when(viewType)
        {
            RECENT_WATCH_VIDEO_KHATAM_QURAN -> R.layout.layout_horizontal_listview_v2
            EDUCATIONAL_VIDEO_KHATAM_QURAN -> R.layout.item_quranic_v2
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater
            .from(parent.context.getLocalContext())
            .inflate(layout, parent, false)


        return  ViewHolder(view,true)


        //return ViewHolder(main_view)
    }

    override fun getItemCount(): Int {
        return when (recentlyWatchedVideos.isNotEmpty()) {
            true -> khatamQuranVideos.size+1
            else -> khatamQuranVideos.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(recentlyWatchedVideos.isNotEmpty() && position == 0)
            RECENT_WATCH_VIDEO_KHATAM_QURAN
        else
            EDUCATIONAL_VIDEO_KHATAM_QURAN
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    internal inner class ViewHolder(itemView: View, private val loaded: Boolean = false) :
        BaseViewHolder(itemView) {
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if (loaded) {
                    when (viewtype) {
                        RECENT_WATCH_VIDEO_KHATAM_QURAN -> {

                            itemView.setPadding(0,0,0,8.dp)
                            KhatamQuranRecentVideo(
                                itemView,
                                itemView.context.getString(R.string.title_continue_watching),
                                itemView.context.getString(R.string.title_continue_watching),
                                recentWatchedList = recentlyWatchedVideos as ArrayList<CommonCardData>
                            )
                        }

                        EDUCATIONAL_VIDEO_KHATAM_QURAN -> {
                            val getData = khatamQuranVideos[if(recentlyWatchedVideos.isNotEmpty()) absoluteAdapterPosition-1 else absoluteAdapterPosition]
                            KhatamQuranVideoList(
                                itemView = itemView,
                                pageTitle = itemView.context.getString(R.string.title_khatam_e_quran),
                                getData = getData,
                                absoluteAdapterPosition = if(recentlyWatchedVideos.isNotEmpty()) absoluteAdapterPosition-1 else absoluteAdapterPosition,
                                itemCount = khatamQuranVideos.size,
                                isShowPlayIcon = true,
                                isShowProgress = false,
                                bannerSize = 1280,
                                itemMarginLeft = 16.dp,
                                itemMarginRight = 16.dp,
                                itemPaddingBottom = 12.dp
                            )
                        }
                    }
            }
        }
    }
}