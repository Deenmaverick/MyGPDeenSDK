package com.deenislamic.sdk.views.adapters.islamiceducationvideo;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.utils.AsyncViewStub
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.prepareStubView
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.islamiceducationvideo.patch.EducationVideoGrid
import com.deenislamic.sdk.views.islamiceducationvideo.patch.IslamicEducationVideo

private const val RECENT_WATCH_VIDEO = 0
private const val EDUCATIONAL_VIDEO = 1
internal class IslamicEducationVideoHomeAdapter(
    private val recentlyWatchedVideos: List<CommonCardData>,
    private val educationVideos: List<CommonCardData>
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootview: AsyncViewStub = main_view.findViewById(R.id.widget)

        when (viewType) {
            RECENT_WATCH_VIDEO -> {
                prepareStubView<View>(
                    rootview.findViewById(R.id.widget),
                    R.layout.layout_horizontal_listview_v2
                ) {

                    onBindViewHolder(ViewHolder(main_view, true), viewType)

                }
            }

            EDUCATIONAL_VIDEO -> {
                prepareStubView<View>(
                    rootview.findViewById(R.id.widget),
                    R.layout.layout_horizontal_listview_v2
                ) {

                    onBindViewHolder(ViewHolder(main_view, true), viewType)

                }
            }

        }

        return ViewHolder(main_view)
    }

    override fun getItemCount(): Int {
        return when (recentlyWatchedVideos.isNotEmpty()) {
            true -> 2
            else -> 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(recentlyWatchedVideos.isNotEmpty() && position == 0)
            RECENT_WATCH_VIDEO
        else
            EDUCATIONAL_VIDEO
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    inner class ViewHolder(itemView: View, private val loaded: Boolean = false) :
        BaseViewHolder(itemView) {
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if (loaded) {
                    when (viewtype) {
                        RECENT_WATCH_VIDEO -> {

                            IslamicEducationVideo(itemView, itemView.context.getString(R.string.recently_played), recentWatchedList = recentlyWatchedVideos as ArrayList<CommonCardData>)
                        }

                        EDUCATIONAL_VIDEO -> {
                            EducationVideoGrid(itemView, itemView.context.getString(R.string.islamic_education_video), educationVideos)
                        }
                    }

            }
        }
    }
}