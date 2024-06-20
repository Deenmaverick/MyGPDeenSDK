package com.deenislamic.sdk.views.dashboard.patch

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislamic.sdk.utils.ProminentLayoutManager
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.setLinearSnapHelper
import com.deenislamic.sdk.views.adapters.dashboard.DashboardBillboardAdapter

internal class Billboard(
    private val widget: View,
    private val billboardData: List<Item>
) {

    private var dashboardBillboardAdapter: DashboardBillboardAdapter? = null
    private val dashboardBillboard: RecyclerView = widget.findViewById(R.id.inf)
    private var linearLayoutManager: ProminentLayoutManager? = null
    private var isAlreadyScrolled: Boolean = false

    init {
        load()
    }

    private fun load() {

        linearLayoutManager = linearLayoutManager ?: ProminentLayoutManager(widget.context, scaleDownBy = 0.09F).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        dashboardBillboardAdapter = DashboardBillboardAdapter(billboardData)

        dashboardBillboard.apply {
            layoutManager = linearLayoutManager
            adapter = dashboardBillboardAdapter
            onFlingListener = null
            setLinearSnapHelper()
            overScrollMode = View.OVER_SCROLL_NEVER
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }
    }

    fun update(data: PrayerTimesResponse) {

        Log.e("prayerData","LOAD")
        dashboardBillboardAdapter?.update(data)

        dashboardBillboard.apply {
            val screenWidth = getScreenWidth()
            post {
                val ptPosition = billboardData.indexOfFirst { bData ->
                    bData.ContentType == "pt"
                }
                isAlreadyScrolled = true
                linearLayoutManager?.scrollToPositionWithOffset(ptPosition, screenWidth.dp)
               // dashboardBillboardAdapter?.notifyDataSetChanged()
            }
        }
    }

    fun updatePrayerTracker(data: Data) {
        dashboardBillboardAdapter?.updatePrayerTracker(data)
        dashboardBillboard.post {
            val ptPosition = billboardData.indexOfFirst { bData ->
                bData.ContentType == "pt"
            }
            dashboardBillboardAdapter?.notifyItemChanged(ptPosition)
        }
    }

    private fun getScreenWidth(): Float {
        return widget.context.resources.displayMetrics.run {
            widthPixels / density - 305
        } / 2
    }

    fun updateState(state: StateModel)
    {
        Log.e("updateStateD","OK")
        dashboardBillboardAdapter?.updateState(state)
    }
}