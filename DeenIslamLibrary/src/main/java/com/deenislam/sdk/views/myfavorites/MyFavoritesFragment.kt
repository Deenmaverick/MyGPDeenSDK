package com.deenislam.sdk.views.myfavorites

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.views.dailydua.FavoriteDuaFragment
import com.deenislam.sdk.views.hadith.HadithFavoriteFragment
import com.deenislam.sdk.views.islamicname.IslamicNameFavFragment
import kotlinx.coroutines.launch

internal class MyFavoritesFragment : BaseRegularFragment(), MaterialButtonHorizontalListCallback {

    private lateinit var header: RecyclerView
    private lateinit var viewPager: ViewPager2
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
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
        val mainview = localInflater.inflate(R.layout.fragment_my_favorites,container,false)

        header = mainview.findViewById(R.id.header)
        viewPager = mainview.findViewById(R.id.viewPager)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.my_favorites),
            backEnable = true,
            view = mainview
        )

        CallBackProvider.setFragment(this)

        setupCommonLayout(mainview)

        return mainview
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
                    pagename = "my_favorite",
                    trackingID = getTrackingID()
                )
            }
        }

        firstload = true

        val headData = arrayListOf(
            Head(0,localContext.getString(R.string.hadith)),
            Head(1,localContext.getString(R.string.daily_dua)),
            Head(2,localContext.getString(R.string.islamic_name))
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
         if (!isDetached) {
            view?.postDelayed({
                initViewPager()
            }, 300)
        }
        else
            initViewPager()

    }

    private fun initViewPager()
    {

        mPageDestination = arrayListOf(
            HadithFavoriteFragment(true),
            FavoriteDuaFragment(true),
            IslamicNameFavFragment(isHideActionBar = true, checkFirstload = true)
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
            offscreenPageLimit = mPageDestination.size
        }

        if (viewPager.getChildAt(0) is RecyclerView) {
            viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER;
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                materialButtonHorizontalAdapter.nextPrev(position)
                header.smoothScrollToPosition(position)
                materialButtonHorizontalAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {
        super.materialButtonHorizontalListClicked(absoluteAdapterPosition)
        viewPager.currentItem = absoluteAdapterPosition

    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "my_favorite",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }

}