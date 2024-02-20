package com.deenislam.sdk.views.mydownloads

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislam.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.quran.QuranDownloadFragment

internal class MyDownloadFragment : BaseRegularFragment(), MaterialButtonHorizontalListCallback {

    private lateinit var header:RecyclerView
    private lateinit var viewPager:ViewPager2
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private var firstload = false
    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_my_download,container,false)

        header = mainview.findViewById(R.id.header)
        viewPager = mainview.findViewById(R.id.viewPager)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.my_downloads),
            backEnable = true,
            view = mainview
        )

        CallBackProvider.setFragment(this)

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headData = arrayListOf(
            Head(0,localContext.getString(R.string.al_quran))
        )

        header.apply {
            val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = linearLayoutManager
            materialButtonHorizontalAdapter = MaterialButtonHorizontalAdapter(
                head = headData,
                activeBgColor = R.color.deen_primary,
                activeTextColor = R.color.deen_white
            )
            adapter = materialButtonHorizontalAdapter
        }

        initView()
    }

    private fun initView()
    {
        if(firstload) {
            initViewPager()
        }
        else if (!isDetached) {
            view?.postDelayed({
                initViewPager()
            }, 300)
        }
        else
            initViewPager()

        firstload = true
    }

    private fun initViewPager()
    {

        mPageDestination = arrayListOf(
            QuranDownloadFragment(true)
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
            mPageDestination
        )

        viewPager.apply {
            adapter = mainViewPagerAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isNestedScrollingEnabled = false
            }
            isUserInputEnabled = true
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        }

        if (viewPager.getChildAt(0) is RecyclerView) {
            viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER;
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                materialButtonHorizontalAdapter.nextPrev(position)
                materialButtonHorizontalAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {
        super.materialButtonHorizontalListClicked(absoluteAdapterPosition)
        viewPager.currentItem = absoluteAdapterPosition

    }

}