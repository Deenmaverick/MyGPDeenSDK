package com.deenislam.sdk.views.dailydua

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.reduceDragSensitivity
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch


internal class DailyDuaFragment : BaseRegularFragment() {

    private lateinit var actionbar: ConstraintLayout
    private lateinit var header: ConstraintLayout
    private lateinit var _viewPager: ViewPager2

    private lateinit var allDuaBtn: MaterialButton
    private lateinit var todayBtn: MaterialButton
    private lateinit var favBtn: MaterialButton

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var firstload:Boolean = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).apply {
            duration = 300L
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_daily_dua,container,false)

        //init view
        actionbar = mainView.findViewById(R.id.actionbar)
        header = mainView.findViewById(R.id.header)
        _viewPager = mainView.findViewById(R.id.viewPager)

        allDuaBtn = mainView.findViewById(R.id.allDuaBtn)
        todayBtn = mainView.findViewById(R.id.todayBtn)
        favBtn = mainView.findViewById(R.id.favBtn)

        setupActionForOtherFragment(0,0,null,localContext.getString(R.string.daily_dua),true,mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.msisdn,
                    pagename = "daily_dua",
                    trackingID = getTrackingID()
                )
            }
        }

        allDuaBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
        allDuaBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))

        if(firstload)
        loadpage()
        else
        view.postDelayed({
            // Code to execute after the animation
            loadpage()
        }, 300)

        firstload = true
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.msisdn,
                    pagename = "daily_dua",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun loadpage()
    {
        mPageDestination = arrayListOf(
            AllDuaFragment(),
            TodayDuaFragment(),
            FavoriteDuaFragment()
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            mPageDestination
        )


        header.post {

            val param = _viewPager.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = header.height + actionbar.height

            _viewPager.layoutParams = param
        }


        _viewPager.apply {
            adapter = mainViewPagerAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isNestedScrollingEnabled = false
            }
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
                        allDuaBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        allDuaBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                    1->
                    {
                        todayBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        todayBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                    2->
                    {
                        favBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        favBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }
                }

            }
        })

        allDuaBtn.setOnClickListener {
            // requireContext().toast("test")


            /* findNavController().navigate(
                 R.id.action_dailyDuaFragment_to_allDuaPreviewFragment)*/

            /* val navOptions = NavOptions.Builder()
                 .setLaunchSingleTop(true)
                 .setPopUpTo(R.id.dailyDuaFragment, false)
                 .build()

             findNavController().navigate(
                 R.id.action_dailyDuaFragment_to_allDuaPreviewFragment,
                 null,
                 navOptions
             )*/

            changeViewPagerPos(0)
        }

        todayBtn.setOnClickListener {
            changeViewPagerPos(1)
        }

        favBtn.setOnClickListener {
            changeViewPagerPos(2)
        }
    }


    private fun clearAllBtnSelection()
    {

        allDuaBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        allDuaBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        todayBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        todayBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        favBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        favBtn.setTextColor(ContextCompat.getColor(requireActivity(),R.color.deen_txt_ash))

    }

    private fun changeViewPagerPos(pos:Int)
    {
        if(pos!=viewPagerPosition)
            _viewPager.setCurrentItem(pos,true)
    }

}