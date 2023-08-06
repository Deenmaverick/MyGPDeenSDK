package com.deenislam.sdk.views.dashboard.patch

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.network.response.dashboard.Banner
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.views.adapters.dashboard.DashboardBillboardAdapter
import com.deenislam.sdk.views.adapters.dashboard.prayerTimeCallback
import java.util.ArrayList

internal class Billboard() {

    private var dashboardBillboardAdapter:DashboardBillboardAdapter ? =null
    private lateinit var  dashboardBillboard: RecyclerView
    private var linearLayoutManager:ProminentLayoutManager ? = null
    private var isAlreadyScrolled:Boolean = false
    companion object
    {
        var instance: Billboard? =null
    }

    fun clearInstance()
    {
        instance = null
    }


    fun getInstance(): Billboard
    {
        if(instance == null)
            instance = Billboard()

        return instance as Billboard
    }

    fun load(
        widget: View,
        viewPool: RecyclerView.RecycledViewPool?,
        callback: prayerTimeCallback? = null,
        dashboardPatchCallback: DashboardPatchCallback
    )
    {
        instance?.dashboardBillboard  = widget.findViewById(R.id.inf)

        instance?.dashboardBillboard?.visibility = View.INVISIBLE

        if(instance?.linearLayoutManager == null)
            instance?.linearLayoutManager = ProminentLayoutManager(widget.context, scaleDownBy = 0.09F)

        instance?.linearLayoutManager?.orientation = LinearLayoutManager.HORIZONTAL
        //instance?.dashboardBillboard?.layoutManager = linearLayoutManager
        //linearLayoutManager.initialPrefetchItemCount = 4

        var screenWidth = widget.context.resources.displayMetrics.run {
            widthPixels / density
        }

        screenWidth -= 305
        screenWidth = (screenWidth/2)

        /*  CoroutineScope(Dispatchers.Main).launch {
              instance?.linearLayoutManager?.scrollToPositionWithOffset(2, screenWidth.dp)
          }*/

        instance?.dashboardBillboardAdapter = DashboardBillboardAdapter(callback,dashboardPatchCallback)

        instance?.dashboardBillboard?.apply {
            layoutManager = instance?.linearLayoutManager
            adapter = instance?.dashboardBillboardAdapter
            dashboardBillboard.onFlingListener = null
            setLinearSnapHelper()
            //pagerSnapHelper.attachToRecyclerView(dashboardBillboard)
            viewPool?.let { setRecycledViewPool(it) }
            //setItemViewCacheSize(3)
            overScrollMode = View.OVER_SCROLL_NEVER
            ViewPagerHorizontalRecyler().getInstance().load(this)

            /*  post {
                  CoroutineScope(Dispatchers.Main).launch {
                      instance?.linearLayoutManager?.scrollToPositionWithOffset(2, screenWidth.dp)
                  }
              }*/

        }
    }

    fun update(data: PrayerTimesResponse)
    {
        instance?.let {

            if(it::dashboardBillboard.isInitialized) {

                //instance?.dashboardBillboardAdapter?.notifyDataSetChanged()

                instance?.dashboardBillboardAdapter?.update(data)


                //if (!instance?.isAlreadyScrolled!!) {

                instance?.dashboardBillboard?.apply {

                    instance?.dashboardBillboard?.visible(true)

                    var screenWidth = this.context.resources.displayMetrics.run {
                        widthPixels / density
                    }

                    screenWidth -= 305
                    screenWidth = (screenWidth / 2)

                    post {

                        instance?.isAlreadyScrolled = true
                        instance?.linearLayoutManager?.scrollToPositionWithOffset(
                            2,
                            screenWidth.dp
                        )

                        instance?.dashboardBillboardAdapter?.notifyDataSetChanged()
                    }

                }

            }

        }
    }

    fun updatePrayerTracker(data: ArrayList<PrayerNotification>)
    {
        if(instance!=null && instance!!::dashboardBillboard.isInitialized) {

            instance?.dashboardBillboardAdapter?.updatePrayerTracker(data)

            instance?.dashboardBillboard?.post {
                instance?.dashboardBillboardAdapter?.notifyDataSetChanged()
            }
        }
    }

    fun updateBillboard(data: List<Banner>)
    {
        if(instance!=null && instance!!::dashboardBillboard.isInitialized) {
            instance?.dashboardBillboardAdapter?.updateBillboard(data)
            instance?.dashboardBillboard?.post {
                instance?.dashboardBillboardAdapter?.notifyDataSetChanged()
            }

        }
    }

}