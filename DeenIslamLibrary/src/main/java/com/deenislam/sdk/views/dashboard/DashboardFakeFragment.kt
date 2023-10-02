package com.deenislam.sdk.views.dashboard

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import kotlinx.coroutines.launch


internal class DashboardFakeFragment : BaseRegularFragment(),otherFagmentActionCallback {

    private lateinit var actionbar: ConstraintLayout
    private lateinit var _viewPager: ViewPager2
    private lateinit var btnBack: AppCompatImageView
    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainview = localInflater.inflate(R.layout.fragment_dashboard_fake,container,false)

        // init view
        actionbar = mainview.findViewById(R.id.actionbar)
        _viewPager = mainview.findViewById(R.id.viewPager)

        setupActionForOtherFragment(R.drawable.ic_menu,0,this@DashboardFakeFragment,localContext.getString(R.string.islamic),false,actionbar)
        btnBack = actionbar.findViewById(R.id.btnBack)
        btnBack.show()

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()

        actionbar.post {
            val param = _viewPager.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin =  actionbar.height
            _viewPager.layoutParams = param
        }

        btnBack.setOnClickListener {
            onBackPress()
        }
    }

    override fun onBackPress() {
        onDestroy()
        destoryDeenSDK()
    }

    private fun initViewPager()
    {
        mPageDestination = arrayListOf(
            DashboardFragment()
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            fragmentManager = requireActivity().supportFragmentManager,
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
        }

        if (_viewPager.getChildAt(0) is RecyclerView) {
            _viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER;
        }

        lifecycleScope.launch {

            userTrackViewModel.trackUser(
                language = getLanguage(),
                msisdn = DeenSDKCore.GetDeenMsisdn(),
                pagename = "home",
                trackingID = get9DigitRandom()
            )
        }

    }

    override fun action1() {
        gotoFrag(R.id.moreFragment)
    }

    override fun action2() {
    }

}