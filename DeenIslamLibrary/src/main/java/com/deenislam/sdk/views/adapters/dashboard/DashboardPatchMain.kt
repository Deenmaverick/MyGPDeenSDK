package com.deenislam.sdk.views.adapters.dashboard;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.prepareStubView
import com.deenislam.sdk.views.adapters.MenuCallback
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.dashboard.patch.AdBanner
import com.deenislam.sdk.views.dashboard.patch.Billboard
import com.deenislam.sdk.views.dashboard.patch.Menu


const val TYPE_WIDGET1:Int = 1
const val TYPE_WIDGET2:Int = 2
const val TYPE_WIDGET3:Int = 3
const val TYPE_WIDGET4:Int = 4
const val TYPE_WIDGET5:Int = 5
const val TYPE_WIDGET6:Int = 6
const val TYPE_WIDGET7:Int = 7
const val TYPE_WIDGET8:Int = 8
internal class DashboardPatchAdapter(
    private val callback: prayerTimeCallback? = null,
    private val menuCallback:MenuCallback ? =null
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    lateinit var  rootview:LinearLayout
    private  var dashlist:ArrayList<Int> = arrayListOf(1,2,3,4,5,6,7,8)

     fun updatePrayerTime(data: PrayerTimesResponse)
    {
        Billboard().getInstance().update(data)
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
                TYPE_WIDGET1 -> {

                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_billboard) {
                        Billboard().getInstance().load(this, null,callback)

                    }

                }
                TYPE_WIDGET2 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_greeting) {
                    }

                }

                TYPE_WIDGET3 -> {

                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_menu) {
                        Menu().getInstance().load(this, null,menuCallback)
                    }
                }

                TYPE_WIDGET4 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_find_umrah) {

                    }
                }

                TYPE_WIDGET5 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_daily_verse) {

                    }
                }

                TYPE_WIDGET6 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_adbanner) {

                        AdBanner().getInstance().load(this, null)
                    }
                }

                TYPE_WIDGET7 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_ramadan) {

                    }
                }

                TYPE_WIDGET8 -> {
                    prepareStubView<View>(main_view.findViewById(R.id.widget),R.layout.dashboard_inc_ramadan_good_deed) {

                    }
                }
            }

        }

        return  ViewHolder(main_view)
    }

    //private fun inflate_widget(layoutInflater: LayoutInflater,layoutInt: Int):View = layoutInflater.inflate(layoutInt,rootview)

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)
        }
    }
}
