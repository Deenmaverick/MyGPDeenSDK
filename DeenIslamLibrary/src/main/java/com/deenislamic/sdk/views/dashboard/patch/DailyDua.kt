package com.deenislamic.sdk.views.dashboard.patch;

import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.DashboardPatchCallback
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislamic.sdk.views.adapters.dailydua.DailyDuaPatchAdapter

internal class DailyDua(private val view: View, private val data: Data) {

    private var dailyDuaPatchAdapter: DailyDuaPatchAdapter? =null
    private var dailyDuaRC: RecyclerView? = null
    private var icon: AppCompatImageView ? = null
    private var titile: AppCompatTextView ? = null
    private val dashboardPatchCallback = CallBackProvider.get<DashboardPatchCallback>()
    private val seeAllBtn:AppCompatTextView = view.findViewById(R.id.seeAllBtn)
    private var callback = CallBackProvider.get<MenuCallback>()


    init {
        load()
    }

    fun load() {

        Log.e("dashboardPatchCallback",dashboardPatchCallback.toString())

        icon = view.findViewById(R.id.icon)
        titile = view.findViewById(R.id.titile)

        if(data.Logo.toString().isEmpty()) {
            icon?.hide()
            titile?.setPadding(8.dp,0,0,0)
        }
        else {
            icon?.let {

                icon?.imageLoad(url = BASE_CONTENT_URL_SGP + data.Logo, placeholder_1_1 = true)

            }
        }


        icon?.apply {

            titile?.text = data.Title
        }


        if(data.FeatureType?.isNotEmpty() == true) {
            seeAllBtn.show()
            seeAllBtn.setOnClickListener {
                callback = CallBackProvider.get<MenuCallback>()
                callback?.menuClicked(data.FeatureType.toString(),null)
            }
        }



        dailyDuaRC = view.findViewById(R.id.listview)
        dailyDuaPatchAdapter = DailyDuaPatchAdapter(dashboardPatchCallback)

        dailyDuaRC?.apply {

            adapter = dailyDuaPatchAdapter
            onFlingListener = null
            PagerSnapHelper().attachToRecyclerView(this)
            ViewPagerHorizontalRecyler().getInstance().load(this)
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        dailyDuaPatchAdapter?.update(data.Items)

    }


}