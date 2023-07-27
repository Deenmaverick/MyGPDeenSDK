package com.deenislam.sdk.views.hadith

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.reduceDragSensitivity
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis

internal class HadithFragment : BaseRegularFragment() {

    private lateinit var hadithBtn:MaterialButton
    private lateinit var catBtn:MaterialButton
    private lateinit var favBtn:MaterialButton
    private lateinit var _viewPager:ViewPager2
    private lateinit var actionbar: ConstraintLayout
    private lateinit var header: LinearLayout

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    override fun OnCreate() {
        super.OnCreate()
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = layoutInflater.inflate(R.layout.fragment_hadith,container,false)

        //init view
        hadithBtn = mainView.findViewById(R.id.hadithBtn)
        catBtn = mainView.findViewById(R.id.catBtn)
        favBtn = mainView.findViewById(R.id.favBtn)
        _viewPager = mainView.findViewById(R.id.viewPager)
        actionbar = mainView.findViewById(R.id.actionbar)
        header = mainView.findViewById(R.id.header)

        setupActionForOtherFragment(0,0,null,"Hadith",true,mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPageDestination = arrayListOf(
            HadithHomeFragment(),
            HadithCategoryFragment(),
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
                        hadithBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.primary))
                        hadithBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                    }

                    1->
                    {
                        catBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(),R.color.primary))
                        catBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                    }

                    2->
                    {
                        favBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.primary))
                        favBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                    }
                }

            }
        })

        hadithBtn.setOnClickListener { _viewPager.currentItem = 0 }
        catBtn.setOnClickListener { _viewPager.currentItem = 1 }
        favBtn.setOnClickListener { _viewPager.currentItem = 2 }
    }

    private fun clearAllBtnSelection()
    {

        hadithBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
        hadithBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))

        catBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
        catBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))

        favBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
        favBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))

    }

}