package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.views.adapters.dailydua.DailyDuaPatchAdapter

internal class DailyDua {

    private var adapter: DailyDuaPatchAdapter? =null
    private var dailyDuaRC: RecyclerView? = null

    companion object {
        var instance: DailyDua? = null
    }

    fun getInstance(): DailyDua {
        if (instance == null)
            instance = DailyDua()

        return instance as DailyDua
    }


    fun load(view: View, dashboardPatchCallback: DashboardPatchCallback)
    {
        val icon: AppCompatImageView = view.findViewById(R.id.icon)
        val titile: AppCompatTextView = view.findViewById(R.id.titile)

        view.setOnClickListener {
            dashboardPatchCallback.dashboardPatchClickd("DailyDua")
        }

        icon.setImageDrawable(
            AppCompatResources.getDrawable(
                icon.context,
                R.drawable.ic_menu_dua
            )
        )
        titile.text = icon.context.getString(R.string.daily_dua)

        instance?.dailyDuaRC = view.findViewById(R.id.listview)
        instance?.adapter = DailyDuaPatchAdapter(dashboardPatchCallback)

        instance?.dailyDuaRC?.apply {

            adapter = instance?.adapter
            onFlingListener = null
            PagerSnapHelper().attachToRecyclerView(this)
            ViewPagerHorizontalRecyler().getInstance().load(this)
            overScrollMode = View.OVER_SCROLL_NEVER
        }

    }

    fun update(dailydua: List<com.deenislam.sdk.service.network.response.dashboard.DailyDua>)
    {
        instance?.adapter?.update(dailydua)
    }
}