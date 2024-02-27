package com.deenislam.sdk.views.islamicboyan

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.reduceDragSensitivity
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch


internal class IslamicBoyanHomeFragment : BaseRegularFragment(), otherFagmentActionCallback {

    private lateinit var boyanBtn: MaterialButton
    private lateinit var catBtn: MaterialButton
    private lateinit var scholarsBtn: MaterialButton
    private lateinit var _viewPager: ViewPager2
    private lateinit var actionbar: ConstraintLayout
    private lateinit var header: ConstraintLayout

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0
    private var firstload = false

    private lateinit var dialog: BottomSheetDialog

    private var isAlreadyRedirect = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainView = localInflater.inflate(R.layout.fragment_islamic_boyan_home, container, false)

        //init view
        boyanBtn = mainView.findViewById(R.id.boyanBtn)
        catBtn = mainView.findViewById(R.id.catBtn)
        scholarsBtn = mainView.findViewById(R.id.scholarsBtn)
        _viewPager = mainView.findViewById(R.id.viewPager)
        actionbar = mainView.findViewById(R.id.actionbar)
        header = mainView.findViewById(R.id.header)

        setupActionForOtherFragment(R.drawable.deen_icon_causion_black,0, this@IslamicBoyanHomeFragment ,localContext.getString(R.string.boyan_video),true, mainView)
        CallBackProvider.setFragment(this)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*if(firstload) {
            loadpage()
        }
        else if (!isDetached) {
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
                    pagename = "islamic_boyan",
                    trackingID = getTrackingID()
                )
            }
        }


            loadpage()

        firstload = true
    }

    private fun loadpage()
    {

        mPageDestination = arrayListOf(
            BoyanHomeFragment(),
            BoyanCategoryFragment(),
            BoyanScholarsFragment()
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
            reduceDragSensitivity(3)


            /*
            post {
                if(navArgs.redirectPage!=-1 && !isAlreadyRedirect) {
                    isAlreadyRedirect = true
                    _viewPager.currentItem = navArgs.redirectPage
                }
            }
            */
        }

        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                viewPagerPosition = position

                clearAllBtnSelection()

                when(position)
                {
                    0-> {
                        boyanBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        boyanBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }

                    1-> {
                        /*
                        if(!BaseApplication.checkUserLoginStatus()) {
                            _viewPager.currentItem = 0
                            _viewPager.post {
                                MainActivity.instance?.askUserLogin()
                            }
                            return
                        }

                        favBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.primary))
                        favBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                        */

                        catBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        catBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))

                    }

                    2 -> {
                        scholarsBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_primary))
                        scholarsBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_white))
                    }
                }

            }
        })

        boyanBtn.setOnClickListener { _viewPager.currentItem = 0 }
        catBtn.setOnClickListener { _viewPager.currentItem = 1 }
        scholarsBtn.setOnClickListener { _viewPager.currentItem = 2 }
        /*
        favBtn.setOnClickListener {
            if(!BaseApplication.checkUserLoginStatus()) {
                MainActivity.instance?.askUserLogin()
                return@setOnClickListener
            }
            else
                _viewPager.currentItem = 1
        }

        */
    }

    private fun clearAllBtnSelection()
    {

        boyanBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        boyanBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        catBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        catBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

        scholarsBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.deen_white))
        scholarsBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_ash))

    }

    override fun action1() {
        showDisclaimerDialog()
    }

    override fun action2() {

    }

    private fun showDisclaimerDialog() {
        dialog = BottomSheetDialog(requireContext())

        val view = layoutInflater.inflate(R.layout.layout_causion, null)

        val cautionText: TextView = view.findViewById(R.id.txtviw_causion)

        cautionText.text = "আমাদের অ্যাপের সকল বয়ান সমূহ ইউটিউব থেকে সংগৃহীত এবং ব্যবহারকারীগণ কোনও চার্জ ছাড়াই এগুলো  দেখতে পারবেন। আমরা ইসলামিক বক্তা ও বিষয়বস্তু সাপেক্ষে এই সেকশনটি তৈরি করেছি, যেন ব্যবহারকারীগণ তাদের সুবিধা মত বিষয়বস্তু অথবা ইসলামিক বক্তা অনুযায়ী খুব সহজেই বয়ানগুলো দেখতে পারেন। \n" +
                "\n" +
                "এই সেকশনের কোন ইসলামিক বক্তার বয়ান যদি আপনার কাছে ব্যবসায়িক কোনও ক্ষতির কারণ বলে মনে হয়, তবে তা  ইমেল এর মাধ্যমে আমাদের জানাতে পারেন (support@deenislamic.com) । আপনার পরিচয় যাচাই করে সঠিক বিবেচিত হলে আমরা ২৪ ঘন্টার মধ্যে সেই  বয়ানগুলো সরিয়ে দেয়ার জন্য প্রয়োজনীয় ব্যবস্থা গ্রহণ করবো ইনশাআল্লাহ!"

        val buttonOk: MaterialButton = view.findViewById(R.id.btn_ok)

        buttonOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(true)

        dialog.setContentView(view)

        dialog.show()
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_boyan",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }

}