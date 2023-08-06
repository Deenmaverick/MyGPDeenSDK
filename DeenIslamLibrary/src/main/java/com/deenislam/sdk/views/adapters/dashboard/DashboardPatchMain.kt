package com.deenislam.sdk.views.adapters.dashboard;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.callback.ViewInflationListener
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.prepareStubView
import com.deenislam.sdk.views.adapters.MenuCallback
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.dashboard.patch.AdBanner
import com.deenislam.sdk.views.dashboard.patch.Billboard
import com.deenislam.sdk.views.dashboard.patch.DailyDua
import com.deenislam.sdk.views.dashboard.patch.DailyVerse
import com.deenislam.sdk.views.dashboard.patch.Greeting
import com.deenislam.sdk.views.dashboard.patch.Hadith
import com.deenislam.sdk.views.dashboard.patch.IslamicName
import com.deenislam.sdk.views.dashboard.patch.Menu
import com.deenislam.sdk.views.dashboard.patch.Tasbeeh
import com.deenislam.sdk.views.dashboard.patch.Zakat


const val TYPE_WIDGET1 = "Banners"
const val TYPE_WIDGET2 = "Greetings"
const val TYPE_WIDGET3 = "Services"
const val TYPE_WIDGET4 = "Verse"
const val TYPE_WIDGET5 = "Qibla"
const val TYPE_WIDGET6 = "Hadith"
const val TYPE_WIDGET7 = "DailyDua"
const val TYPE_WIDGET8 = "Zakat"
const val TYPE_WIDGET9 = "Tasbeeh"
const val TYPE_WIDGET10 = "IslamicName"
/*const val TYPE_WIDGET8:Int = 8
const val TYPE_WIDGET9:Int = 9*/
internal class DashboardPatchAdapter(
    private val callback: prayerTimeCallback? = null,
    private val menuCallback:MenuCallback ? =null,
    private var viewInflationListener: ViewInflationListener,
    private val dashboardPatchCallback: DashboardPatchCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    lateinit var  rootview:LinearLayout
    private  var dashlist:ArrayList<String> = arrayListOf()
    private var dashboardData:Data ? = null
    private var prayerTimesResponse:PrayerTimesResponse ? =null
    private var inflatedViewCount:Int = 0


     fun updatePrayerTime(data: PrayerTimesResponse)
    {
        Billboard().getInstance().update(data)
        Greeting().getInstance().update(data)
    }

    fun updateDashData(data: Data)
    {
        dashlist.clear()
        dashlist.addAll(data.ActiveItems)
        dashboardData = data

        notifyDataSetChanged()
    }

    fun updatePrayerTracker(data: java.util.ArrayList<PrayerNotification>)
    {
        Billboard().getInstance().updatePrayerTracker(data)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        rootview = main_view.findViewById(R.id.rootview)

        dashlist.forEach()
        {
            when(it) {

                // Billboard
                TYPE_WIDGET1 -> {

                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_billboard) {
                        completeViewLoad()
                        Billboard().getInstance().load(this, null,callback,dashboardPatchCallback).apply {

                            dashboardData?.Banners?.let {
                                    banner ->
                                Billboard().getInstance().updateBillboard(banner)
                            }
                        }
                    }
                }

                // greetings
                TYPE_WIDGET2 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_greeting) {
                        completeViewLoad()
                        Greeting().getInstance().load(this,null)

                    }
                }

                // menu
                TYPE_WIDGET3 -> {

                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_menu) {
                        completeViewLoad()
                        Menu().getInstance().load(this, null,menuCallback)
                        dashboardData?.Services?.let {
                                menu ->
                            Menu().getInstance().update(menu)
                        }
                    }
                }


                // daily verse
                TYPE_WIDGET4 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_item_quranic) {
                        completeViewLoad()
                        dashboardData?.VerseData?.let {
                                 verse ->
                            DailyVerse().getInstance().load(this,verse,dashboardPatchCallback)
                        }

                    }
                }


                // ad banner
                TYPE_WIDGET5 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_adbanner) {
                        completeViewLoad()
                        AdBanner().getInstance().load(this, null,dashboardPatchCallback)
                        dashboardData?.Qibla?.let {
                            adbanner->
                            AdBanner().getInstance().update(adbanner)
                        }
                    }
                }

                // hadith

                TYPE_WIDGET6 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_item_quranic) {
                        completeViewLoad()
                        dashboardData?.Hadith?.let {
                            hadith->
                            Hadith().getInstance().load(this,hadith,dashboardPatchCallback)
                        }
                    }
                }

                // DailyDua

                TYPE_WIDGET7 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_item_horizontal_list) {
                        completeViewLoad()
                        DailyDua().getInstance().load(this,dashboardPatchCallback)
                        dashboardData?.DailyDua?.let {
                                dailydua->
                            DailyDua().getInstance().update(dailydua)
                        }
                    }
                }

                // zakat calculator

                TYPE_WIDGET8 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_item_quranic) {
                        completeViewLoad()
                        dashboardData?.Zakat?.let {
                                zakat->
                            Zakat().getInstance().load(this,zakat,dashboardPatchCallback)
                        }

                    }
                }

                // digital tasbeeh

                TYPE_WIDGET9 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_item_quranic) {
                        completeViewLoad()
                        dashboardData?.Tasbeeh?.let {
                                tasbeeh->
                            Tasbeeh().getInstance().load(this,tasbeeh,dashboardPatchCallback)
                        }
                    }
                }

                // islamic name

                TYPE_WIDGET10 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_item_quranic) {
                        completeViewLoad()
                        dashboardData?.IslamicName?.let {
                                name->
                            IslamicName().getInstance().load(this,name,dashboardPatchCallback)
                        }

                    }
                }
            }
        }

        return  ViewHolder(main_view)
    }

    private fun completeViewLoad()
    {
        inflatedViewCount++
        if (inflatedViewCount >= dashlist.size-1) {
            viewInflationListener.onAllViewsInflated()
        }
    }

    //private fun inflate_widget(layoutInflater: LayoutInflater,layoutInt: Int):View = layoutInflater.inflate(layoutInt,rootview)

    override fun getItemCount(): Int = if(dashboardData !=null) 1 else -1

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    internal inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)
        }
    }
}
