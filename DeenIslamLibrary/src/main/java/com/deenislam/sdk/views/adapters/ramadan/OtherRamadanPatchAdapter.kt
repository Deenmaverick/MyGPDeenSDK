package com.deenislam.sdk.views.adapters.ramadan;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.ViewInflationListener
import com.deenislam.sdk.service.models.ramadan.StateModel
import com.deenislam.sdk.service.network.response.ramadan.Data
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.prepareStubView
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.ramadan.patch.RamadanDua
import com.deenislam.sdk.views.ramadan.patch.RamadanTable
import com.deenislam.sdk.views.ramadan.patch.RamadanTrackCard
import com.deenislam.sdk.views.ramadan.patch.StateList

internal class OtherRamadanPatchAdapter(
    private val data: Data,
    private val stateArray: ArrayList<StateModel>,
    private var selectedState: StateModel?
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var inflatedViewCount: Int = 0
    private val viewCallback = CallBackProvider.get<ViewInflationListener>()

    //view class instance
    private var ramadanTrackCard: RamadanTrackCard? = null

    // Patch instance
    private var stateList: StateList? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        val mainView = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootView: AsyncViewStub = mainView.findViewById(R.id.widget)

        when (viewType) {

            0-> {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.item_dropdown) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                }
            }

            1-> {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.item_ramadan_other_day_card) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                }
            }

            2-> {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.item_ramadan_time_table_card) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                }
            }

            3-> {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                }
            }

        }

        return  ViewHolder(mainView)
    }

    fun updateFastingTrack(fasting: Boolean)
    {
        ramadanTrackCard?.updateFastingTrack(fasting)
    }

    private fun completeViewLoad() {
        inflatedViewCount++
        if (inflatedViewCount >= itemCount) {
            viewCallback?.onAllViewsInflated()
        }
    }

    override fun getItemCount(): Int = 4

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    fun updateDropdownSelectedState(stateModel: StateModel)
    {
        selectedState = stateModel
        stateList?.updateSelectedState(stateModel)
    }

    inner class ViewHolder(itemView: View,private val loaded:Boolean = false) : BaseViewHolder(itemView) {

       override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if(loaded) {
                when (viewtype) {
                    0 -> {

                        (itemView.layoutParams as? MarginLayoutParams)?.topMargin = 16.dp

                        stateList = StateList(itemView,stateArray)
                        selectedState?.let {
                            stateList?.updateSelectedState(it)
                        }

                    }

                    1 -> {

                        ramadanTrackCard = RamadanTrackCard(itemView,data.FastTracker)
                    }

                    2 -> {

                        RamadanTable(itemView,data.FastTime,data.FastTracker.islamicDate)
                    }

                    3 -> {

                        RamadanDua(itemView,data.RamadanDua)
                    }
                }

                completeViewLoad()
            }
        }
    }
}