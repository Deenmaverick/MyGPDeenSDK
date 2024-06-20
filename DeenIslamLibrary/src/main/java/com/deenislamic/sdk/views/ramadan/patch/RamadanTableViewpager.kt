package com.deenislamic.sdk.views.ramadan.patch

import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.ramadan.FastTime
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.views.adapters.ramadan.RamadanViewPagerAdapter
import com.google.android.material.button.MaterialButton

internal class RamadanTableViewpager(private val itemView: View, fastTime: List<FastTime>, dateArabic:String) {

    private val viewPager: ViewPager2 = itemView.findViewById(R.id.viewPager)
    private val rahamatDay:MaterialButton = itemView.findViewById(R.id.rahamatDay)
    private val maghfiratDay:MaterialButton = itemView.findViewById(R.id.maghfiratDay)
    private val najatDay:MaterialButton = itemView.findViewById(R.id.najatDay)

    private val ramadanTimeSub: AppCompatTextView = itemView.findViewById(R.id.ramadanTimeSub)

    init {

        val context = itemView.context

        val listOfSets: List<List<FastTime>> = fastTime.chunkIntoSets(10)

        var serialCounter = 1

        listOfSets.forEach { set ->
            // Now you can use the serialNumber as needed, for example, updating FastTime objects
            set.forEach { fastTime ->
                val serialNumber = String.format("%02d", serialCounter)
                fastTime.islamicDate = serialNumber
                serialCounter++
            }
        }

            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 12.dp

           ramadanTimeSub.text = dateArabic
        viewPager.apply {
            adapter = RamadanViewPagerAdapter(listOfSets,true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isNestedScrollingEnabled = false
            }
            isUserInputEnabled = true
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            }



        rahamatDay.setOnClickListener {
            if(viewPager.currentItem!=0)
                viewPager.setCurrentItem(0,true)
        }

        maghfiratDay.setOnClickListener {
            if(viewPager.currentItem!=1)
                viewPager.setCurrentItem(1,true)
        }

        najatDay.setOnClickListener {
            if(viewPager.currentItem!=2)
                viewPager.setCurrentItem(2,true)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                clearAllBtnSelection()

                when(position)
                {
                    0->
                    {
                        rahamatDay.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.deen_white))
                        rahamatDay.setTextColor(ContextCompat.getColor(context,R.color.deen_primary))
                    }

                    1->
                    {
                        maghfiratDay.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.deen_white))
                        maghfiratDay.setTextColor(ContextCompat.getColor(context,R.color.deen_primary))

                    }

                    2->
                    {
                        najatDay.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.deen_white))
                        najatDay.setTextColor(ContextCompat.getColor(context,R.color.deen_primary))
                    }

                }

            }
        })

        val indexOfSet = listOfSets.indexOfFirst { set ->
            set.any { fastTime -> fastTime.isToday }
        }

        if(indexOfSet!=-1)
            viewPager.setCurrentItem(indexOfSet,false)
    }

    private fun List<FastTime>.chunkIntoSets(chunkSize: Int): List<List<FastTime>> {
        return if (chunkSize > 0) {
            this.chunked(chunkSize)
        } else {
            listOf()
        }
    }

    private fun clearAllBtnSelection()
    {
        val context = itemView.context

        rahamatDay.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.deen_background))
        rahamatDay.setTextColor(ContextCompat.getColor(context,R.color.deen_txt_ash))

        maghfiratDay.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.deen_background))
        maghfiratDay.setTextColor(ContextCompat.getColor(context,R.color.deen_txt_ash))

        najatDay.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.deen_background))
        najatDay.setTextColor(ContextCompat.getColor(context,R.color.deen_txt_ash))


    }
}