package com.deenislam.sdk.views.zakat

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

internal class ZakatFragment : BaseRegularFragment() {


    private lateinit var zakatBtn: MaterialButton
    private lateinit var savedBtn: MaterialButton
    private lateinit var _viewPager: ViewPager2
    private lateinit var actionbar: ConstraintLayout
    private lateinit var header: LinearLayout

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var firstload = false


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
        val mainView = localInflater.inflate(R.layout.fragment_zakat,container,false)

        //init view
        zakatBtn = mainView.findViewById(R.id.zakatBtn)
        savedBtn = mainView.findViewById(R.id.savedBtn)
        _viewPager = mainView.findViewById(R.id.viewPager)
        actionbar = mainView.findViewById(R.id.actionbar)
        header = mainView.findViewById(R.id.header)

        setupActionForOtherFragment(0,0,null,localContext.getString(R.string.zakat),true,mainView)
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
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "zakat",
                    trackingID = getTrackingID()
                )
            }
        }



     /*   if(firstload)
        loadPage()
        else
        view.postDelayed({
            // Code to execute after the animation
            loadPage()
        }, 300)

        firstload = true*/

        loadPage()

    }

    private fun loadPage()
    {

        mPageDestination = arrayListOf(
            ZakatHomeFragment(),
            ZakatSavedFragment()
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
            offscreenPageLimit = 2
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
                        zakatBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        zakatBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                    1->
                    {
                        savedBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        savedBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                }

            }
        })

        zakatBtn.setOnClickListener { _viewPager.currentItem = 0 }
        savedBtn.setOnClickListener { _viewPager.currentItem = 1 }
    }

    private fun clearAllBtnSelection()
    {
        zakatBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        zakatBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        savedBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        savedBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "zakat",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }


    }


}