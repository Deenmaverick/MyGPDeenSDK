package com.deenislam.sdk.views.quran

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
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


internal class QuranFragment : BaseRegularFragment(), MaterialButtonHorizontalListCallback {

    private lateinit var header: RecyclerView
    private lateinit var _viewPager: ViewPager2
    private lateinit var actionbar:ConstraintLayout
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var firstload =false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainview = localInflater.inflate(R.layout.fragment_quran,container,false)

        // init view
        actionbar = mainview.findViewById(R.id.actionbar)
        header = mainview.findViewById(R.id.header)
        _viewPager = mainview.findViewById(R.id.viewPager)
        /*surahBtn = mainview.findViewById(R.id.surahBtn)
        juzBtn = mainview.findViewById(R.id.juzBtn)
        myquranBtn = mainview.findViewById(R.id.myquranBtn)
        quranHomeBtn = mainview.findViewById(R.id.quranHomeBtn)*/

        CallBackProvider.setFragment(this)

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackPressCallback(this@QuranFragment)

        val headData = arrayListOf(
            Head(0,localContext.getString(R.string.home)),
            Head(1,localContext.getString(R.string.sura)),
            Head(2,localContext.getString(R.string.juz)),
            Head(3,localContext.getString(R.string.my_quran))
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

    fun getActionbar(): ConstraintLayout = actionbar



   /* override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible) {
            setupBackPressCallback(this@QuranFragment)
            initView()

            when (val getfragment =
                childFragmentManager.findFragmentByTag("f${_viewPager.currentItem}")) {
                is QuranHomeFragment -> getfragment.setupActionBar()
                is QuranSurahFragment -> getfragment.setupActionBar()
                is QuranJuzFragment -> getfragment.setupActionBar()
                is MyQuranFragment -> getfragment.setupActionBar()
            }
        }
    }*/



    private fun initView()
    {
        if(firstload)
            return
        firstload = true

        initViewPager()

    }


    private fun initViewPager()
    {

        mPageDestination = arrayListOf(
            QuranHomeFragment(),
            QuranSurahFragment(),
            QuranJuzFragment(),
            MyQuranFragment()
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
            mPageDestination
        )

        _viewPager.apply {
            adapter = mainViewPagerAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isNestedScrollingEnabled = false
            }
            isUserInputEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        }

        if (_viewPager.getChildAt(0) is RecyclerView) {
            _viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER;
        }

        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewPagerPosition = position

            }
        })
    }


    private fun changeViewPagerPos(pos:Int)
    {
        if(pos!=viewPagerPosition)
            _viewPager.setCurrentItem(pos,true)
    }

    override fun onBackPress() {
        if(!isVisible)
            super.onBackPress()
        else
            changeMainViewPager(0)
    }

    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {
        _viewPager.setCurrentItem(absoluteAdapterPosition,true)
        materialButtonHorizontalAdapter.notifyItemChanged(absoluteAdapterPosition)
        header.smoothScrollToPosition(absoluteAdapterPosition)
    }

}