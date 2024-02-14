package com.deenislam.sdk.views.hadith

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
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

internal class HadithFragment : BaseRegularFragment() {

    private lateinit var hadithBtn:MaterialButton
    private lateinit var catBtn:MaterialButton
    private lateinit var favBtn:MaterialButton
    private lateinit var _viewPager:ViewPager2
    private lateinit var actionbar: ConstraintLayout
    private lateinit var header: ConstraintLayout

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_hadith,container,false)

        //init view
        hadithBtn = mainView.findViewById(R.id.hadithBtn)
        catBtn = mainView.findViewById(R.id.catBtn)
        favBtn = mainView.findViewById(R.id.favBtn)
        _viewPager = mainView.findViewById(R.id.viewPager)
        actionbar = mainView.findViewById(R.id.actionbar)
        header = mainView.findViewById(R.id.header)

        setupActionForOtherFragment(0,0,null,localContext.getString(R.string.hadith),true,mainView)
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
                        pagename = "hadith",
                        trackingID = getTrackingID()
                    )
                }
        }

        hadithBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
        hadithBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))

       /* if(firstload)
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
            HadithChapterFragment(),
            HadithFavoriteFragment()
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
                        hadithBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        hadithBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                    1->
                    {
                        favBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        favBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))

                    }

                }

            }
        })

        hadithBtn.setOnClickListener { _viewPager.currentItem = 0 }
        favBtn.setOnClickListener { _viewPager.currentItem = 1 }
    }


    override fun onBackPress() {

        Log.e("onBackPress","HADITH")
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "hadith",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }

    private fun clearAllBtnSelection()
    {

        hadithBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        hadithBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        favBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        favBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

    }

}