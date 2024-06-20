package com.deenislamic.sdk.views.adapters.ramadan;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.ramadan.Data
import com.deenislamic.sdk.service.network.response.ramadan.FastTracker
import com.deenislamic.sdk.service.weakref.ramadan.RamadanInstance
import com.deenislamic.sdk.utils.AsyncViewStub
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.prepareStubView
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.dashboard.patch.SingleCardList
import com.deenislamic.sdk.views.ramadan.patch.RamadanMenuPatch
import com.deenislamic.sdk.views.ramadan.patch.RamadanTableViewpager
import com.deenislamic.sdk.views.ramadan.patch.RamadanTrackCard
import com.deenislamic.sdk.views.ramadan.patch.StateList

internal class RamadanPatchAdapter(
    private val data: Data,
    private val stateArray: ArrayList<StateModel>,
    private var selectedState: StateModel?,
    private val patch: List<com.deenislamic.sdk.service.network.response.dashboard.Data>? = null
) : RecyclerView.Adapter<BaseViewHolder>() {

        private var inflatedViewCount: Int = 0
        private val viewCallback = CallBackProvider.get<ViewInflationListener>()


        // Patch instance
        private var stateList: StateList? = null

        private var patchPos = -1
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
                    prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.item_ramadan_time_viewpager) {
                        onBindViewHolder(ViewHolder(mainView,true),viewType)
                    }
                }

                else -> {

                    patchPos++

                    val patchData = patch?.get(patchPos)

                    when(patchData?.AppDesign){

                        "menu" -> {
                            val postPatchPos = patchPos
                            prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview) {
                                onBindViewHolder(ViewHolder(
                                    itemView = mainView,
                                    loaded = true,
                                    getpatchPos = postPatchPos
                                ),viewType)
                            }
                        }

                        "CommonCardList" -> {
                            val postPatchPos = patchPos
                            prepareStubView<View>(rootView, R.layout.layout_horizontal_listview_v2) {
                                onBindViewHolder(ViewHolder(
                                    itemView = mainView,
                                    loaded = true,
                                    getpatchPos = postPatchPos
                                ), viewType)
                            }
                        }

                    }
                }


            }

            if(viewType == itemCount - 1) {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_footer) {

                }
            }

            return  ViewHolder(mainView)
        }

        fun updateFastingTrack(fasting: Boolean)
        {
            RamadanInstance.getRamadanCardInstance()?.updateFastingTrack(fasting)
        }

    fun getFastingTrackData(): FastTracker? {
        return RamadanInstance.getRamadanCardInstance()?.getTrackData()
    }

        private fun completeViewLoad() {
            inflatedViewCount++
            if (inflatedViewCount >= itemCount) {
                viewCallback?.onAllViewsInflated()
            }
        }

        override fun getItemCount(): Int = patch?.size?.plus(3)?:3

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.onBind(position,getItemViewType(position))
        }

        fun updateDropdownSelectedState(stateModel: StateModel) {
            selectedState = stateModel
            stateList?.updateSelectedState(stateModel)
        }

        inner class ViewHolder(
            itemView: View,
            private val loaded:Boolean = false,
            private val getpatchPos:Int = -1
        ) : BaseViewHolder(itemView) {

            init {
                Log.e("RamadanMenuPatch",getpatchPos.toString())
            }
            override fun onBind(position: Int, viewtype: Int) {
                super.onBind(position, viewtype)

                if(loaded) {
                    when (viewtype) {
                        0 -> {

                            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 16.dp
                            itemView.setPadding(16.dp,0,16.dp,0)
                            stateList = StateList(itemView,stateArray)
                            selectedState?.let {
                                stateList?.updateSelectedState(it)
                            }

                        }

                        1 -> {
                            itemView.setPadding(16.dp,0,16.dp,0)
                            RamadanInstance.updateRamadanCard(RamadanTrackCard(itemView,data.FastTracker))
                        }

                        2 -> {
                            itemView.setPadding(16.dp,0,16.dp,0)
                            RamadanTableViewpager(itemView,data.FastTime,data.FastTracker.islamicDate)
                        }

                        else -> {

                            if(getpatchPos!=-1) {

                                val patchData = patch?.get(getpatchPos)

                                when (patchData?.AppDesign) {

                                    "menu" -> {

                                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 12.dp
                                        itemView.setPadding(16.dp,0,16.dp,0)
                                        RamadanMenuPatch(itemView, patchData.Items)
                                    }

                                    "CommonCardList" -> {
                                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                                        SingleCardList(
                                            itemView,
                                            patchData
                                        ).load()
                                    }
                                }
                            }
                        }
                    }

                    completeViewLoad()
                }
            }
        }

    }