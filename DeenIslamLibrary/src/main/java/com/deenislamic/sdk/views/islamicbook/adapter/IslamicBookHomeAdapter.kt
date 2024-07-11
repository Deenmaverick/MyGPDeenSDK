package com.deenislamic.sdk.views.islamicbook.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.models.islamicbook.BookDownloadPayload
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.AsyncViewStub
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.prepareStubView
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class IslamicBookHomeAdapter(
    private val data: List<Data>
): RecyclerView.Adapter<BaseViewHolder>()  {

    private var viewInflationListener = CallBackProvider.get<ViewInflationListener>()
    private var inflatedViewCount: Int = 0
    private var newBooksPatch: NewBooksPatch? = null
    private var popularBooksPatch: NewBooksPatch? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mainView = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootView: AsyncViewStub = mainView.findViewById(R.id.widget)

        when (data[viewType].Design) {

            "NewBooks"-> {
                Log.e("onCreateViewHolderPdf",data[viewType].Design)
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                    completeViewLoad()
                }
            }

            "PopularBooks"-> {
                Log.e("onCreateViewHolderPdf",data[viewType].Design)
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                    completeViewLoad()
                }
            }
        }

        return ViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    private fun completeViewLoad() {
        inflatedViewCount++
        if (inflatedViewCount >= 2) {
            viewInflationListener?.onAllViewsInflated()
        }
        //viewInflationListener.onAllViewsInflated()
    }

    fun update(payload: BookDownloadPayload, fileName: String?){
        newBooksPatch?.update(payload, fileName)
        popularBooksPatch?.update(payload, fileName)
    }

    inner class ViewHolder(itemView: View, private val loaded: Boolean = false) :
        BaseViewHolder(itemView) {


        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if(loaded)
            {

                val getData = data[position]

                when(data[viewtype].Design)
                {
                    "NewBooks"-> {
                        Log.e("TheBookAdapterAdapter",data[viewtype].Design)
                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        newBooksPatch = NewBooksPatch(view = itemView, title = getData.Title, items = getData.Items)
                        newBooksPatch?.load()
                    }

                    "PopularBooks"-> {
                        Log.e("TheBookAdapterAdapter",data[viewtype].Design)
                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        popularBooksPatch = NewBooksPatch(view = itemView, title = getData.Title, items = getData.Items)
                        popularBooksPatch?.load()
                    }
                }
            }
        }
    }
}