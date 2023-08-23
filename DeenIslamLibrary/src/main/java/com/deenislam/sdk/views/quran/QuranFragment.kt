package com.deenislam.sdk.views.quran

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch


internal class QuranFragment : BaseRegularFragment() {

    private lateinit var actionbar:ConstraintLayout
    private lateinit var header:ConstraintLayout
    private lateinit var _viewPager: ViewPager2

    private lateinit var surahBtn:MaterialButton
    private lateinit var juzBtn:MaterialButton
    //private lateinit var myquranBtn:MaterialButton
    private lateinit var quranHomeBtn:AppCompatImageView

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var firstload =false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

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
        surahBtn = mainview.findViewById(R.id.surahBtn)
        juzBtn = mainview.findViewById(R.id.juzBtn)
        //myquranBtn = mainview.findViewById(R.id.myquranBtn)
        quranHomeBtn = mainview.findViewById(R.id.quranHomeBtn)

        setupActionForOtherFragment(0,0,null,localContext.resources.getString(R.string.al_quran),true,actionbar)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        loadpage()
        startPostponedEnterTransition()
    }

    private fun loadpage()
    {

        if(!firstload) {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.msisdn,
                    pagename = "quran",
                    trackingID = getTrackingID()
                )
            }
        }
        firstload = true


        initViewPager()

        header.post {
            val param = _viewPager.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = _viewPager.marginTop+ header.height
            _viewPager.layoutParams = param
        }

        actionbar.post {
            val param = _viewPager.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = _viewPager.marginTop+ actionbar.height
            _viewPager.layoutParams = param
        }

        quranHomeBtn.setOnClickListener {
            changeViewPagerPos(0)
        }

        surahBtn.setOnClickListener {
            changeViewPagerPos(1)
        }

        juzBtn.setOnClickListener {
            changeViewPagerPos(2)
        }

        /*  myquranBtn.setOnClickListener {
              changeViewPagerPos(3)
          }*/


    }


    override fun onBackPress() {
        Log.e("onBackPress", "QURAN HOME")

        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.msisdn,
                    pagename = "quran",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }


    private fun initViewPager()
    {
        mPageDestination = arrayListOf(
            QuranHomeFragment(),
            QuranSurahFragment(),
            QuranJuzFragment(),
            //MyQuranFragment(actionbar)
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
            offscreenPageLimit = 3
        }

        if (_viewPager.getChildAt(0) is RecyclerView) {
            _viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER;
        }

        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                viewPagerPosition = position

                clearAllBtnSelection()

                when(position)
                {
                    0-> quranHomeBtn.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_quran_verse))
                    1->
                    {
                        surahBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        surahBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                    2->
                    {
                        juzBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        juzBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                  /*  3->
                    {
                        myquranBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.primary))
                        myquranBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                    }*/
                }

            }
        })

    }

    fun getActionbar():ConstraintLayout = actionbar

    private fun clearAllBtnSelection()
    {

        quranHomeBtn.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_quran_verse_inactive))

        surahBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        surahBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        juzBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        juzBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

      /*  myquranBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
        myquranBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
*/

    }

    private fun changeViewPagerPos(pos:Int)
    {
        if(pos!=viewPagerPosition)
            _viewPager.setCurrentItem(pos,true)
    }

}