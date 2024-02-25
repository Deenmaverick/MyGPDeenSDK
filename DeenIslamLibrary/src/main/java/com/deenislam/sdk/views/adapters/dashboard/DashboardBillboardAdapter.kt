package com.deenislam.sdk.views.adapters.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.models.ramadan.StateModel
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.dashboard.patch.PrayerTime
import com.deenislam.sdk.service.weakref.dashboard.DashboardBillboardPatchClass
import com.google.gson.Gson

private const val PRAYER_VIEW = 0
private const val BILLBOARD_VIEW = 1

internal class DashboardBillboardAdapter(
    private val BillBoardData: List<Item>
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val dashboardPatchCallback = CallBackProvider.get<DashboardPatchCallback>()

    private var prayerData: PrayerTimesResponse? = null

    private var filterBillboardData = if (prayerData == null)
        BillBoardData.filter { it.ContentType != "pt" }
    else
        BillBoardData

    private var countryState = ""

    fun update(
        data: PrayerTimesResponse
    ) {
        prayerData = data

        filterBillboardData = BillBoardData

        DashboardBillboardPatchClass.getPrayerTimeInstance()?.updatePrayerTime(data)
    }

    fun updatePrayerTracker(data: Data) {
        prayerData?.Data?.WaktTracker?.forEach {

            when (it.Wakt) {
                "Fajr" -> it.status = data.Fajr
                "Zuhr" -> it.status = data.Zuhr
                "Asar" -> it.status = data.Asar
                "Maghrib" -> it.status = data.Maghrib
                "Isha" -> it.status = data.Isha
            }

        }

        /*notifyDataSetChanged()*/
    }

    fun updateState(state: StateModel) {
        DashboardBillboardPatchClass.getPrayerTimeInstance()?.stateSelected(state)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val layout = when (viewType) {
            PRAYER_VIEW -> R.layout.item_dashboard_prayer_time
            BILLBOARD_VIEW -> R.layout.item_dashboard_billboard
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater
            .from(parent.context.getLocalContext())
            .inflate(layout, parent, false)


        return ViewHolder(view)
    }


    override fun getItemViewType(position: Int): Int {

        /*if(position == ceil(itemCount.toDouble() / 2).toInt())*/
        return if (filterBillboardData[position].ContentType == "pt" && prayerData != null)
            PRAYER_VIEW
        else
            BILLBOARD_VIEW

    }

    override fun getItemCount(): Int {
        return filterBillboardData.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val billboardBanner: AppCompatImageView? by lazy { itemView.findViewById(R.id.appCompatImageView2) }
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)


            when (viewtype) {

                PRAYER_VIEW -> {


                        DashboardBillboardPatchClass.updatePrayerTime(PrayerTime(itemView, prayerData))
                        DashboardBillboardPatchClass.getPrayerTimeInstance()?.load()

                }

                BILLBOARD_VIEW -> {

                    val billboardData = filterBillboardData[position]
                    billboardBanner?.imageLoad(
                        url = billboardData.contentBaseUrl + "/" + billboardData.imageurl1,
                        placeholder_1_1 = true,
                        customMemoryKey = "billboard_"+billboardData.Id.toString()
                    )

                    billboardBanner?.setOnClickListener {

                        dashboardPatchCallback?.dashboardPatchClickd(
                            billboardData.ContentType,
                            data = billboardData
                        )
                    }
                }

            }

            when (position) {
                0 -> {
                    (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp
                    (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 4.dp
                }

                itemCount - 1 -> {
                    (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 16.dp
                    (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 4.dp
                }

                else -> {
                    (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 4.dp
                    (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 4.dp
                }
            }

        }
    }
}

internal interface PrayerTimeCallback {
    fun nextPrayerCountownFinish()
    fun allPrayerPage()

    fun prayerTask(momentName: String?, b: Boolean)

    fun billboard_prayer_load_complete()
}
