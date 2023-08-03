package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Qibla
import com.deenislam.sdk.utils.DotsIndicatorDecoration
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.views.adapters.CarouselAdapter

internal class AdBanner {

    private var adapter:CarouselAdapter ? =null
    private var adbanner:RecyclerView ? = null
    companion object
    {
        var instance: AdBanner? = null
    }


    fun getInstance(): AdBanner {
        if (instance == null)
            instance = AdBanner()

        return instance as AdBanner
    }

    fun load(widget: View, viewPool: RecyclerView.RecycledViewPool?) {

         instance?.adbanner = widget.findViewById(R.id.inf)
        instance?.adapter = CarouselAdapter()


        adbanner?.apply {

            adapter = instance?.adapter
            onFlingListener = null
            PagerSnapHelper().attachToRecyclerView(instance?.adbanner)
            val radius = 6
            val dotsHeight = 56
            addItemDecoration(
                DotsIndicatorDecoration(
                    radius,
                    radius * 4,
                    dotsHeight,
                    ContextCompat.getColor(widget.context, R.color.gray),
                    ContextCompat.getColor(widget.context, R.color.txt_black)
                )
            )

            viewPool?.let { setRecycledViewPool(it) }

            ViewPagerHorizontalRecyler().getInstance().load(this)
            overScrollMode = View.OVER_SCROLL_NEVER
        }

    }

    fun update(adbanner: List<Qibla>)
    {
        instance?.adapter?.update(adbanner)

    }
}