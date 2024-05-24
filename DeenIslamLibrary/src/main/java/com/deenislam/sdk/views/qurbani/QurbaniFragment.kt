package com.deenislam.sdk.views.qurbani

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton

internal class QurbaniFragment : BaseRegularFragment() {

    private lateinit var viewPager:ViewPager2
    private lateinit var catBtn:MaterialButton
    private lateinit var bookmarkBtn:MaterialButton
    private lateinit var actionbar:ConstraintLayout
    private lateinit var header:LinearLayout

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var firstload = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_qurbani,container,false)

        viewPager = mainview.findViewById(R.id.viewPager)
        catBtn = mainview.findViewById(R.id.catBtn)
        bookmarkBtn = mainview.findViewById(R.id.bookmarkBtn)
        actionbar = mainview.findViewById(R.id.actionbar)
        header = mainview.findViewById(R.id.header)
        header.hide()

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.qurbani),
            backEnable = true,
            view = mainview
        )

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPageDestination = arrayListOf(
            QurbaniCategoryFragment(),
            QurabniBookmarkFragment()
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            mPageDestination
        )


      /*  header.post {

            val param = viewPager.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = header.height + actionbar.height

            viewPager.layoutParams = param
        }*/


        viewPager.apply {
            adapter = mainViewPagerAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isNestedScrollingEnabled = false
            }
            isUserInputEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            reduceDragSensitivity(2)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                catBtn.setInactiveState()
                bookmarkBtn.setInactiveState()

                when(position)
                {
                    0-> {
                        catBtn.setActiveState()
                    }

                    1-> {
                        bookmarkBtn.setActiveState()
                    }
                }

            }
        })

        catBtn.setOnClickListener {

            viewPager.setCurrentItem(0,true)
        }


        bookmarkBtn.setOnClickListener {
             if(!Subscription.isSubscribe){
                gotoFrag(R.id.action_global_subscriptionFragment)
                return@setOnClickListener
            }
            else
                viewPager.setCurrentItem(1,true)
        }

    }

}