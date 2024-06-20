package com.deenislamic.sdk.views.adapters.qurbani;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.AsyncViewStub
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.prepareStubView
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.dashboard.patch.SingleCardList
import com.deenislamic.sdk.views.qurbani.patch.QurbaniMenuPatch

internal class QurbaniCategoryAdapter(private val data: List<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        val mainView = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootView: AsyncViewStub = mainView.findViewById(R.id.widget)

        when (data[viewType].AppDesign) {

                    "menu" -> {
                        prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview) {
                            onBindViewHolder(ViewHolder(
                                itemView = mainView,
                                loaded = true
                            ),viewType)
                        }
                    }

                    "CommonCardList" -> {
                        prepareStubView<View>(rootView, R.layout.layout_horizontal_listview_v2) {
                            onBindViewHolder(ViewHolder(
                                itemView = mainView,
                                loaded = true
                            ), viewType)
                        }
                    }


        }

        return  ViewHolder(mainView,false)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    inner class ViewHolder(
        itemView: View,
        private val loaded:Boolean = false
    ) : BaseViewHolder(itemView) {

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            val getdata = data[position]
            if(loaded) {
                when (getdata.AppDesign) {

                                "menu" -> {
                                    Log.e("QurbaniCategory","called")
                                    (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 12.dp
                                    itemView.setPadding(16.dp,0,16.dp,0)
                                    QurbaniMenuPatch(itemView, getdata.Items)
                                }

                                "CommonCardList" -> {
                                    (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                                    SingleCardList(
                                        itemView,
                                        getdata
                                    ).load()
                                }
                            }

            }
        }
    }
}