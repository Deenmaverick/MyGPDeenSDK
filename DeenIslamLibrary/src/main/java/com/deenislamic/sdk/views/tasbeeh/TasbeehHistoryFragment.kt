package com.deenislamic.sdk.views.tasbeeh

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.reduceDragSensitivity
import com.deenislamic.sdk.views.adapters.MainViewPagerAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton

internal class TasbeehHistoryFragment : BaseRegularFragment() {

    private lateinit var _viewPager: ViewPager2

    private lateinit var dailyBtn: MaterialButton
    private lateinit var weeklyBtn: MaterialButton
    private lateinit var monthlyBtn: MaterialButton

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var firstload = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainView = localInflater.inflate(R.layout.fragment_tasbeeh_history,container,false)

        //init view
        _viewPager = mainView.findViewById(R.id.viewPager)

        dailyBtn = mainView.findViewById(R.id.dailyBtn)
        weeklyBtn = mainView.findViewById(R.id.weeklyBtn)
        monthlyBtn = mainView.findViewById(R.id.monthlyBtn)


        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.count_history),
            backEnable = true,
            view = mainView
        )

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadpage()
    }

    private fun loadpage()
    {

        if(firstload)
            return
        firstload = true


        mPageDestination = arrayListOf(
            TasbeehTodayCountFragment(),
            TasbeehWeeklyCountFragment(),
            TasbeehMonthlyCountFragment()
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            mPageDestination
        )


        _viewPager.apply {
            adapter = mainViewPagerAdapter
            isNestedScrollingEnabled = false
            isUserInputEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            reduceDragSensitivity(2)
        }

        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                viewPagerPosition = position

                clearAllBtnSelection()

                when(position)
                {
                    0->
                    {
                        dailyBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        dailyBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                    1->
                    {
                        weeklyBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        weeklyBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white,))
                    }

                    2->
                    {
                        monthlyBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        monthlyBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }
                }
            }
        })

        dailyBtn.setOnClickListener {

            changeViewPagerPos(0)
        }

        weeklyBtn.setOnClickListener {
            changeViewPagerPos(1)
        }

        monthlyBtn.setOnClickListener {
           changeViewPagerPos(2)
        }
    }


    private fun clearAllBtnSelection()
    {

        dailyBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        dailyBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        weeklyBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        weeklyBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        monthlyBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        monthlyBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

    }

    private fun changeViewPagerPos(pos:Int)
    {
        if(pos!=viewPagerPosition)
            _viewPager.setCurrentItem(pos,true)
    }
}