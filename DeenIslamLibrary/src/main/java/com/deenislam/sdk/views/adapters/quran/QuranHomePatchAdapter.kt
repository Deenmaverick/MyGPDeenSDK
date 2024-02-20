package com.deenislam.sdk.views.adapters.quran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.prepareStubView
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.quran.patch.RecentRead
import com.deenislam.sdk.views.quran.patch.PopularSurah

internal class QuranHomePatchAdapter(private val data: List<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val RECENT_READ = "RecentQuran"
    private val POPULAR_SURAH = "PopularSurah"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mainView = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootView: AsyncViewStub = mainView.findViewById(R.id.widget)

        when (data[viewType].Design) {
            (RECENT_READ) -> {

                if(data[viewType].Items.isNotEmpty()) {
                    prepareStubView<View>(
                        rootView.findViewById(R.id.widget),
                        R.layout.layout_horizontal_listview_v2
                    ) {
                        onBindViewHolder(ViewHolder(mainView, true), viewType)
                    }
                }
            }

            POPULAR_SURAH -> {
                prepareStubView<View>(
                    rootView.findViewById(R.id.widget),
                    R.layout.layout_horizontal_listview_v2
                ) {
                    onBindViewHolder(ViewHolder(mainView, true), viewType)
                }
            }
        }

        if(viewType == itemCount - 1) {
            prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_footer) {

            }
        }

        return ViewHolder(mainView)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    inner class ViewHolder(itemView: View, private val loaded: Boolean = false) :
        BaseViewHolder(itemView) {
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if(loaded)
            {
                val getData = data[position]

                when(getData.Design)
                {
                    RECENT_READ ->
                    {
                        RecentRead(itemView,getData.Items)
                    }

                    POPULAR_SURAH ->
                    {
                        PopularSurah(itemView,getData.Items)
                    }
                }
            }
        }
    }
}