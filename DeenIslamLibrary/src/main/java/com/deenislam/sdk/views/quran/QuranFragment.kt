package com.deenislam.sdk.views.quran

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.main.MainActivity
import com.deenislam.sdk.views.main.actionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialFadeThrough


class QuranFragment : BaseRegularFragment(),actionCallback {

    private val header:LinearLayout by lazy { requireView().findViewById(R.id.header) }
    private val _viewPager: ViewPager2 by lazy { requireView().findViewById(R.id.viewPager) }

    private val surahBtn:MaterialButton by lazy { requireView().findViewById(R.id.surahBtn) }
    private val juzBtn:MaterialButton by lazy { requireView().findViewById(R.id.juzBtn) }
    private val myquranBtn:MaterialButton by lazy { requireView().findViewById(R.id.myquranBtn) }
    private val quranHomeBtn:AppCompatImageView by lazy { requireView().findViewById(R.id.quranHomeBtn) }

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var firstload:Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_quran,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.post {

            val param = _viewPager.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = header.height

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

        myquranBtn.setOnClickListener {
            changeViewPagerPos(3)
        }

    }

  /*  override fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)
        if (visible) {
            initView()
        }
    }*/

    override fun onResume() {
        super.onResume()

        /*if(MainActivity.instance?.bottomNavClicked == true)
            animateView()
        else*/
            initView()
        setupAction(R.drawable.ic_search,R.drawable.ic_settings,this@QuranFragment,"AL Quran")

    }

    private fun animateView()
    {
        val anim: Animation

        if(MainActivity.instance?.childFragmentAnimForward == true)
            anim = AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left)
        else
            anim = AnimationUtils.loadAnimation(requireContext(), R.anim.left_to_right)

        requireView().startAnimation(anim)

        anim?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                initView()
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })

        MainActivity.instance?.resetBottomNavClick()
    }

    private fun initView()
    {
        if(firstload != 0)
            return
        firstload = 1

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
            fragmentManager = getChildFragmentManager(),
            lifecycle = lifecycle,
            mPageDestination
        )

        _viewPager.apply {
            adapter = mainViewPagerAdapter
            isNestedScrollingEnabled = false
            isUserInputEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = 1
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
                        surahBtn.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.primary,requireContext().theme))
                        surahBtn.setTextColor(requireContext().resources.getColor(R.color.white,requireContext().theme))
                    }

                    2->
                    {
                        juzBtn.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.primary,requireContext().theme))
                        juzBtn.setTextColor(requireContext().resources.getColor(R.color.white,requireContext().theme))
                    }

                    3->
                    {
                        myquranBtn.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.primary,requireContext().theme))
                        myquranBtn.setTextColor(requireContext().resources.getColor(R.color.white,requireContext().theme))
                    }
                }

            }
        })
    }

    private fun clearAllBtnSelection()
    {

        quranHomeBtn.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_quran_verse_inactive))

        surahBtn.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.white,requireContext().theme))
        surahBtn.setTextColor(requireContext().resources.getColor(R.color.txt_ash,requireContext().theme))

        juzBtn.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.white,requireContext().theme))
        juzBtn.setTextColor(requireContext().resources.getColor(R.color.txt_ash,requireContext().theme))

        myquranBtn.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.white,requireContext().theme))
        myquranBtn.setTextColor(requireContext().resources.getColor(R.color.txt_ash,requireContext().theme))


    }

    private fun changeViewPagerPos(pos:Int)
    {
        if(pos!=viewPagerPosition)
            _viewPager.setCurrentItem(pos,true)
    }

    override fun action1() {

    }

    override fun action2() {

    }

}