package com.deenislamic.sdk.views.islamimasaIl

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.IslamiMasailResource
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislamic.sdk.service.repository.IslamiMasailRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.IslamiMasailViewModel
import com.deenislamic.sdk.views.adapters.MainViewPagerAdapter
import com.deenislamic.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import kotlinx.coroutines.launch


internal class IslamiMasailFragment : BaseRegularFragment(),
    otherFagmentActionCallback, MaterialButtonHorizontalListCallback {

    private lateinit var header:RecyclerView
    private lateinit var viewPager:ViewPager2
    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter
    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var firstload = false
    private lateinit var viewmodel: IslamiMasailViewModel

    override fun OnCreate() {
        super.OnCreate()

        setupBackPressCallback(this,true)

        val repository = IslamiMasailRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())

        val factory = VMFactory(repository)
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[IslamiMasailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_islami_masail,container,false)

        viewPager = mainview.findViewById(R.id.viewPager)
        header = mainview.findViewById(R.id.header)

        setupActionForOtherFragment(
            action1 = R.drawable.deen_ic_plus_button_round,
            action2 = 0,
            callback = this,
            actionnBartitle = localContext.getString(R.string.islami_masail),
            backEnable = true,
            view = mainview
        )

        CallBackProvider.setFragment(this)

        setupCommonLayout(mainview)


        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_masail",
                    trackingID = getTrackingID()
                )
            }
        }

        firstload = true

            loadpage()

    }

    override fun onResume() {
        super.onResume()
        CallBackProvider.setFragment(this)
    }

    private fun loadpage(){

        val headData = arrayListOf(
            Head(0,localContext.getString(R.string.all_questions)),
            Head(1,localContext.getString(R.string.category_masail)),
            Head(2,localContext.getString(R.string.bookmark_masail)),
            Head(3,localContext.getString(R.string.my_question))
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

        mPageDestination = arrayListOf(
            MasailAllQuestionFragment(),
            MasailQuestionCatFragment(),
            MasailBookmarkFragment(),
            MasailMyQuestionFragment()
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

        initObserver()

    }

    private fun initObserver(){

        viewmodel.islamiMasailMainLivedata.observe(viewLifecycleOwner){
            when(it){
                is IslamiMasailResource.postQuestion ->{
                    viewmodel.clearMainLive()
                    if(viewPager.currentItem!=3) {
                        viewPager.post {
                            viewPager.setCurrentItem(3, true)
                        }
                    }
                }
            }
        }
    }

    override fun action1() {
        gotoFrag(R.id.action_global_createMasailQuestionFragment)
    }

    override fun action2() {

    }


    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_masail",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }

    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {
        viewPager.setCurrentItem(absoluteAdapterPosition,true)
        materialButtonHorizontalAdapter.notifyItemChanged(absoluteAdapterPosition)
        header.smoothScrollToPosition(absoluteAdapterPosition)
    }

    inner class VMFactory(
        private val islamiMasailRepository: IslamiMasailRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IslamiMasailViewModel(islamiMasailRepository) as T
        }
    }

}