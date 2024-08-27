package com.deenislamic.sdk.views.prayerlearning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.reduceDragSensitivity
import com.deenislamic.sdk.utils.setActiveState
import com.deenislamic.sdk.utils.setInactiveState
import com.deenislamic.sdk.views.adapters.MainViewPagerAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton

internal class PrayerVisualFragment : BaseRegularFragment() {

    private lateinit var maleBtn:MaterialButton
    private lateinit var femaleBtn:MaterialButton
    private lateinit var viewPager:ViewPager2

    private val navArgs:PrayerVisualFragmentArgs by navArgs()

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_prayer_visual,container,false)

        maleBtn = mainView.findViewById(R.id.maleBtn)
        femaleBtn = mainView.findViewById(R.id.femaleBtn)
        viewPager = mainView.findViewById(R.id.viewPager)

        setupActionForOtherFragment(0,0,null,navArgs.title,true,mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        mPageDestination = arrayListOf(
            PrayerLearningDetailsFragment.newInstance(
                Bundle().apply {
                    putString("gender","male")
                }
            ),
            PrayerLearningDetailsFragment.newInstance(
                Bundle().apply {
                    putString("gender","female")
                }
            )
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            mPageDestination
        )

        viewPager.apply {
            adapter = mainViewPagerAdapter
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            reduceDragSensitivity(2)
        }

        viewPager.post {
            viewPager.currentItem = navArgs.page
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                viewPagerPosition = position

                maleBtn.setInactiveState()
                femaleBtn.setInactiveState()


                when(position)
                {
                    0-> maleBtn.setActiveState()

                    1-> femaleBtn.setActiveState()
                }

            }
        })

        maleBtn.setOnClickListener { viewPager.currentItem = 0 }
        femaleBtn.setOnClickListener {
            viewPager.currentItem = 1
        }

    }

}