package com.deenislamic.sdk.views.adapters.dashboard;

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.DashboardPatchCallback
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.libs.advertisement.Advertisement
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.weakref.dashboard.DashboardPatchClass
import com.deenislamic.sdk.utils.AsyncViewStub
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.prepareStubView
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.dashboard.patch.Allah99Names
import com.deenislamic.sdk.views.dashboard.patch.Billboard
import com.deenislamic.sdk.views.dashboard.patch.Compass
import com.deenislamic.sdk.views.dashboard.patch.DailyDua
import com.deenislamic.sdk.views.dashboard.patch.Greeting
import com.deenislamic.sdk.views.dashboard.patch.Menu
import com.deenislamic.sdk.views.dashboard.patch.QuranicItem
import com.deenislamic.sdk.views.dashboard.patch.SingleCardList
import com.deenislamic.sdk.views.dashboard.patch.Tasbeeh


private const val TYPE_WIDGET1 = "Banners"
private const val TYPE_WIDGET2 = "Wish"
private const val TYPE_WIDGET3 = "Services"
private const val TYPE_WIDGET4 = "DailyVerse"
private const val TYPE_WIDGET5 = "Hadith"
private const val TYPE_WIDGET6 = "Dua"
const val TYPE_WIDGET7 = "Compass"
private const val TYPE_WIDGET8 = "Zakat"
private const val TYPE_WIDGET9 = "Tasbeeh"
private const val TYPE_WIDGET10 = "IslamicName"
const val TYPE_WIDGET11 = "99NameOfAllah"
private const val TYPE_WIDGET12 = "RecentQuran"
private const val TYPE_WIDGET13 = "HajjPreReg"
private const val TYPE_WIDGET14 = "SeriesQuranLearnPatch"
private const val PATCH_TASBEEH = "DigitalTasbeeh"
private const val PATCH_FAVORITE = "Favcontent"
private const val PATCH_SINGLE_CARD = "SingleCard"
private const val PATCH_SINGLE_CARD_LIST = "CommonCardList"
private const val PATCH_WIDGET = "widgetPatch"
private const val PATCH_SINGLE_IMAGE = "SingleImage"
internal class DashboardPatchAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private var viewInflationListener = CallBackProvider.get<ViewInflationListener>()

    private var inflatedViewCount: Int = 0
    private var totalsize = 0

    private var DashboardData: ArrayList<Data> =
        arrayListOf()

    private var prayerTimesResponse: PrayerTimesResponse? = null

    private val dashboardPatchCallback = CallBackProvider.get<DashboardPatchCallback>()


    fun getDashboardData() = DashboardData

    fun getAllah99NameInstance() = DashboardPatchClass.getAllah99NamesInstance()


        fun updatePrayerTime(
            data: PrayerTimesResponse
        ) {
            // Log.e("prayerData",billboard.toString())
            prayerTimesResponse = data
            DashboardPatchClass.getBillboardInstance()?.update(data)

           /* if (getViewTypePosition(TYPE_WIDGET1) > 0)
                notifyItemChanged(getViewTypePosition(TYPE_WIDGET1))*/

        }

        fun updateBillboardPrayer(
            data: PrayerTimesResponse,
        ) {
            DashboardPatchClass.getBillboardInstance()?.update(data)

        }

        fun updatePrayerTracker(data: com.deenislamic.sdk.service.network.response.prayertimes.tracker.Data) {
            DashboardPatchClass.getBillboardInstance()?.updatePrayerTracker(data)
        }

        fun updateDashData(data: List<Data>, totalsize: Int) {
            /*inflatedViewCount = 0
            DashboardData.clear()*/
            this.totalsize = totalsize
            DashboardData.addAll(data)

            notifyItemRangeInserted(itemCount, data.size)

            //notifyDataSetChanged()
        }

        fun updateState(state: StateModel) {
            Log.e("updateStatePatch",DashboardPatchClass.getBillboardInstance().toString())
            DashboardPatchClass.getBillboardInstance()?.updateState(state)
        }


        fun updateCompass(degree: Float, degreeTxt: String) {
            Log.e("updateCompass", "CALLED")
            DashboardPatchClass.getCompassInstance()?.updateCompass(degree, degreeTxt)
        }

        fun updateCompass(distance: String) {
            DashboardPatchClass.getCompassInstance()?.updateCompass(distance)
        }


        @SuppressLint("SuspiciousIndentation")
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            val main_view = LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.layout_async_match, parent, false)

            val rootview: AsyncViewStub = main_view.findViewById(R.id.widget)


            when (DashboardData[viewType].AppDesign) {

                TYPE_WIDGET1 -> {
                    prepareStubView<View>(rootview, R.layout.dashboard_inc_billboard) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                TYPE_WIDGET2 -> {
                    prepareStubView<View>(rootview, R.layout.dashboard_inc_greeting) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }

                }

                TYPE_WIDGET3 -> {
                    prepareStubView<View>(rootview, R.layout.dashboard_inc_menu) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }

                    /*val imageAd = Advertisement.getImageAd()
                    imageAd?.let {
                        prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                            loadAdvertisement(this,it)
                        }
                    }*/
                }

                TYPE_WIDGET4 -> {
                    prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                TYPE_WIDGET5 -> {

                    prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }

                }

                TYPE_WIDGET6 -> {
                    prepareStubView<View>(rootview, R.layout.dashboard_inc_item_horizontal_list) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                TYPE_WIDGET7 -> {
                    prepareStubView<View>(rootview, R.layout.dashboard_inc_compass) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                TYPE_WIDGET8 -> {
                    prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                TYPE_WIDGET9 -> {
                    prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                TYPE_WIDGET10 -> {
                    prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                TYPE_WIDGET11 -> {
                    prepareStubView<View>(rootview, R.layout.dashboard_inc_allah_99_names) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }

                    /*val imageAd = Advertisement.getImageAd()
                    imageAd?.let {
                        prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                            loadAdvertisement(this,it)
                        }
                    }*/
                }

               /* TYPE_WIDGET12 -> {
                    prepareStubView<View>(rootview, R.layout.dashboard_inc_item_horizontal_list) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }*/

               /* TYPE_WIDGET13 -> {
                    prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }*/

                TYPE_WIDGET14 -> {
                    prepareStubView<View>(rootview, R.layout.layout_horizontal_listview_v2) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                PATCH_TASBEEH -> {
                    prepareStubView<View>(rootview, R.layout.item_patch_tasbeeh) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                /*PATCH_FAVORITE -> {
                    prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                        onBindViewHolder(ViewHolder(main_view,true),viewType)
                    }
                }*/

                PATCH_SINGLE_CARD -> {
                    prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view,true),viewType)
                    }

                    /*if(DashboardData[viewType].Items.isNotEmpty()){
                        if(DashboardData[viewType].Items[0].ContentType == "hdd"){
                            val imageAd = Advertisement.getImageAd()
                            imageAd?.let {
                                prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                                    loadAdvertisement(this,it)
                                }
                            }
                        }
                    }*/
                }

                PATCH_SINGLE_CARD_LIST -> {
                    prepareStubView<View>(rootview, R.layout.layout_horizontal_listview_v2) {
                        onBindViewHolder(ViewHolder(main_view, true), viewType)
                    }
                }

                PATCH_WIDGET -> {
                    prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view,true),viewType)
                    }
                }

                PATCH_SINGLE_IMAGE -> {
                    prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_quranic_v1) {
                        onBindViewHolder(ViewHolder(main_view,true),viewType)
                    }
                }

            }

            if(totalsize>0 && viewType == totalsize - 1) {

               /* val imageAd = Advertisement.getImageAd()
                imageAd?.let {
                    prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                        loadAdvertisement(this,it)
                    }
                }*/

                prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.layout_footer) {
                    this.setOnClickListener {  }
                }
            }


            completeViewLoad()

            return ViewHolder(main_view, false)
        }


        override fun getItemCount(): Int = DashboardData.size /*if(DashboardData.size>0) 1 else 0*/

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.onBind(position, getItemViewType(position))
        }

        override fun getItemViewType(position: Int): Int {

            return position
        }


        fun getViewTypePosition(viewtype: String): Int =
            DashboardData.indexOfFirst { bData ->
                bData.Design == viewtype
            }


        private fun completeViewLoad() {
            inflatedViewCount++
            if (inflatedViewCount >= 0) {
                viewInflationListener?.onAllViewsInflated()
            }
            //viewInflationListener.onAllViewsInflated()
        }

    private fun loadAdvertisement(
        itemview: View,
        data: com.deenislamic.sdk.service.network.response.advertisement.Data
    ) {
        data.let {
            (itemview.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
            val helper = QuranicItem(itemview)
            helper.loadImageAd(it)
        }
    }

        inner class ViewHolder(
            itemView: View,
            private val loaded: Boolean = false,
            private val imageAd:com.deenislamic.sdk.service.network.response.advertisement.Data?=null
        ) :
            BaseViewHolder(itemView) {
            override fun onBind(position: Int, viewtype: Int) {
                super.onBind(position, viewtype)

                Log.e("ImageAD",imageAd.toString()+viewtype+loaded)

                if(loaded) {
                    val data = DashboardData[position]

                    when (data.AppDesign) {

                        TYPE_WIDGET1 -> {

                            DashboardPatchClass.updateBillboard(
                                Billboard(
                                    itemView, data.Items
                                )
                            )

                           /* prayerTimesResponse?.let {
                                DashboardPatchClass.getBillboardInstance()?.update(it)
                            }*/
                            prayerTimesResponse?.let {
                                DashboardPatchClass.getBillboardInstance()?.update(
                                    it
                                )
                            }

                        }

                        TYPE_WIDGET2 -> {
                            Greeting(itemView, data.Items)
                        }

                        TYPE_WIDGET3 -> {

                            Menu(itemView, data.Items)
                        }

                        TYPE_WIDGET4 -> {
                            val helper = QuranicItem(itemView)
                            helper.loadDailyVerse(data.Items)

                        }

                        TYPE_WIDGET5 -> {
                            val helper = QuranicItem(itemView)
                            helper.loadHadith(data.Items)

                        }

                        TYPE_WIDGET6 -> {
                            DailyDua(itemView, data)
                        }

                        TYPE_WIDGET7 -> {
                            DashboardPatchClass.updateCompass(Compass(itemView))
                            DashboardPatchClass.getCompassInstance()?.load(data.Items)
                        }

                        TYPE_WIDGET8 -> {
                            val helper = QuranicItem(itemView)
                            helper.loadZakat(data.Items)
                        }

                        TYPE_WIDGET9 -> {
                            val helper = QuranicItem(itemView)
                            helper.loadTasbeeh(data.Items)
                        }

                        TYPE_WIDGET10 -> {
                            val helper = QuranicItem(itemView)
                            helper.loadIslamicName(data.Items)

                        }

                        TYPE_WIDGET11 -> {
                            DashboardPatchClass.updateAllah99Names(Allah99Names(itemView))
                            DashboardPatchClass.getAllah99NamesInstance()?.load(data.Items)
                        }

                        /*TYPE_WIDGET12 -> {
                            val helper = RecentQuran(itemView, data)
                            helper.load()
                        }*/

                        /*TYPE_WIDGET13 -> {
                            val helper = QuranicItem(itemView)
                            helper.loadHajjPrereg(data.Items)
                        }*/

                        TYPE_WIDGET14 -> {
                            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                            SingleCardList(
                                itemView,
                                data
                            ).load()
                        }

                        PATCH_TASBEEH -> {

                            DashboardPatchClass.updateTasbeeh(Tasbeeh(itemView))
                            DashboardPatchClass.getTasbeehInstance()?.load()

                            itemView.setOnClickListener {
                                dashboardPatchCallback?.dashboardPatchClickd("tb", data.Items[0].copy(DuaId = DashboardPatchClass.getTasbeehInstance()?.selectedPos?:0))
                            }

                        }

                       /* PATCH_FAVORITE -> {

                            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                            Favorite(
                                itemView,
                                data.Title,
                                items = data.Items,
                                iconDrawable = R.drawable.ic_favorite_primary_fill
                            ).load()

                        }*/

                        PATCH_SINGLE_CARD -> {
                            val helper = QuranicItem(itemView)
                            helper.loadSingleCard(data.Items)
                        }

                        PATCH_SINGLE_CARD_LIST -> {
                            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                            SingleCardList(
                                itemView,
                                data
                            ).load()
                        }

                        PATCH_WIDGET -> {
                            val helper = QuranicItem(itemView)
                            helper.widget(data.Items)
                        }

                        PATCH_SINGLE_IMAGE -> {
                            val helper = QuranicItem(itemView)
                            helper.loadSingleImage(data.Items)
                        }

                    }

                }
            }
        }
    }