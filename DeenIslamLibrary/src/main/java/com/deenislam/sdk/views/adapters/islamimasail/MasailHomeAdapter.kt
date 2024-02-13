package com.deenislam.sdk.views.adapters.islamimasail;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.ViewInflationListener
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.prepareStubView
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.islamimasaIl.patch.LastQuesntions

internal class MasailHomeAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private val VIEW_RECENT = "MasailRecent"
    private val VIEW_NEW_QUESTION = "MasailNew"

    private var viewInflationListener = CallBackProvider.get<ViewInflationListener>()

    private var inflatedViewCount: Int = 0

    private var data: ArrayList<Data> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mainView = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootView: AsyncViewStub = mainView.findViewById(R.id.widget)


        when (data[viewType].AppDesign) {
            VIEW_RECENT -> {

                if(data[viewType].Items.isNotEmpty()) {
                    prepareStubView<View>(
                        rootView.findViewById(R.id.widget),
                        R.layout.layout_horizontal_listview_v2
                    ) {
                        onBindViewHolder(ViewHolder(mainView, true), viewType)
                    }
                }

            }

            VIEW_NEW_QUESTION -> {
                prepareStubView<View>(
                    rootView.findViewById(R.id.widget),
                    R.layout.layout_horizontal_listview_v2
                ) {
                    onBindViewHolder(ViewHolder(mainView, true), viewType)
                }
            }
        }

        completeViewLoad()

        return ViewHolder(mainView,false)
    }

    private fun completeViewLoad() {
        inflatedViewCount++
        if (inflatedViewCount >= itemCount) {
            viewInflationListener?.onAllViewsInflated()
        }
        //viewInflationListener.onAllViewsInflated()
    }

    fun update(data: List<Data>) {
        this.data.clear()
        this.data.addAll(data)
        notifyItemRangeInserted(itemCount, this.data.size)
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

            if (loaded) {
                val getData = data[position]
                when (data[viewtype].AppDesign) {
                    VIEW_RECENT -> {
                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        LastQuesntions(
                            view = itemView,
                            title = itemView.context.getString(R.string.recently_read),
                            items = getData.Items,
                            customviewtype = 2
                        ).load()
                    }

                    VIEW_NEW_QUESTION -> {

                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        LastQuesntions(itemView,getData.Title,
                            items = getData.Items
                        ).load()
                    }
                }
            }
        }
    }
}