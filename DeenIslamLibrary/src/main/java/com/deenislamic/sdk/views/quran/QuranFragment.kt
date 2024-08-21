package com.deenislamic.sdk.views.quran

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.views.adapters.MainViewPagerAdapter
import com.deenislamic.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class QuranFragment : BaseRegularFragment(), MaterialButtonHorizontalListCallback {

    private lateinit var header: RecyclerView
    private lateinit var _viewPager: ViewPager2
    private lateinit var actionbar:ConstraintLayout
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var firstload =false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
    }

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

        header.itemAnimator = null

        setupActionForOtherFragment(0,0,null,localContext.resources.getString(R.string.al_quran),true,mainview)


        CallBackProvider.setFragment(this)

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setupBackPressCallback(this@QuranFragment)

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "quran",
                    trackingID = getTrackingID()
                )
            }
        }

        firstload = true


        loadpage()

    }

    private fun loadpage(){

        /*if(firstload)
            return
        firstload = true*/

        if(!this::materialButtonHorizontalAdapter.isInitialized) {
            val headData = arrayListOf(
                Head(0, localContext.getString(R.string.home)),
                Head(1, localContext.getString(R.string.sura)),
                Head(2, localContext.getString(R.string.juz)),
                Head(3, localContext.getString(R.string.my_quran))
            )

            materialButtonHorizontalAdapter = MaterialButtonHorizontalAdapter(
                head = headData,
                activeBgColor = R.color.deen_primary,
                activeTextColor = R.color.deen_white
            )

        }


        header.apply {
            val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = linearLayoutManager

            adapter = materialButtonHorizontalAdapter
        }


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
            offscreenPageLimit = mPageDestination.size
        }

        if (_viewPager.getChildAt(0) is RecyclerView) {
            _viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER;
        }

        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                /*if(position == 3){
                    if(!Subscription.isSubscribe){
                        gotoFrag(R.id.action_global_subscriptionFragment)
                        _viewPager.setCurrentItem(0,true)
                        return
                    }
                }*/
                viewPagerPosition = position


            }
        })

        setupGlobalMiniPlayerForHome(0)

    }

    fun getActionbar(): ConstraintLayout = actionbar

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "quran",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {
       /* if(absoluteAdapterPosition == 3){
            if(!Subscription.isSubscribe){
                gotoFrag(R.id.action_global_subscriptionFragment)
                materialButtonHorizontalAdapter.nextPrev(viewPagerPosition)
                return
            }
        }*/
        _viewPager.setCurrentItem(absoluteAdapterPosition,true)
        materialButtonHorizontalAdapter.notifyItemChanged(absoluteAdapterPosition)
        header.smoothScrollToPosition(absoluteAdapterPosition)
    }

}