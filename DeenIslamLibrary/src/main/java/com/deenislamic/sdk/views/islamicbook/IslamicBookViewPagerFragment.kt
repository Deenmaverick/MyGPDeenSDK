package com.deenislamic.sdk.views.islamicbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.views.adapters.MainViewPagerAdapter
import com.deenislamic.sdk.views.adapters.common.MaterialButtonHorizontalAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

internal class IslamicBookViewPagerFragment : BaseRegularFragment(),
    MaterialButtonHorizontalListCallback, otherFagmentActionCallback {

    private lateinit var header: RecyclerView
    private lateinit var _viewPager: ViewPager2

    private lateinit var materialButtonHorizontalAdapter: MaterialButtonHorizontalAdapter

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var firstload =false

    private lateinit var dialog: BottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_islamic_book_view_pager,container,false)

        // init view
        header = mainview.findViewById(R.id.header)
        _viewPager = mainview.findViewById(R.id.viewPager)

        setupActionForOtherFragment(R.drawable.deen_icon_causion_black,0,this@IslamicBookViewPagerFragment,localContext.getString(R.string.islamic_book),true, mainview)

        CallBackProvider.setFragment(this)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headData = arrayListOf(
            Head(0,localContext.getString(R.string.books)),
            Head(1,localContext.getString(R.string.categories)),
            Head(2,localContext.getString(R.string.authors)),
            Head(4,localContext.getString(R.string.download_list)),
            Head(3,localContext.getString(R.string.favorite))
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

        initViewPager()
    }


    private fun initViewPager()
    {

        mPageDestination = arrayListOf(
            IslamicBookHomeFragment(),
            IslamicBookCategoryFragment(),
            IslamicBookAuthorsFragment(),
            DownloadedBookFragment(),
            FavoriteBookFragment()
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
            mPageDestination
        )

        _viewPager.apply {
            adapter = mainViewPagerAdapter
            isNestedScrollingEnabled = false
            isUserInputEnabled = true
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        }

        if (_viewPager.getChildAt(0) is RecyclerView) {
            _viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER;
        }

        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {


                when(position)
                {
                    0-> {
                        materialButtonHorizontalAdapter.nextPrev(0)
                    }
                    1-> {
                        materialButtonHorizontalAdapter.nextPrev(1)
                    }
                    2 -> {
                        materialButtonHorizontalAdapter.nextPrev(2)
                    }
                    3 -> {

                         if(!Subscription.isSubscribe){
                                gotoFrag(R.id.action_global_subscriptionFragment)
                                _viewPager.setCurrentItem(viewPagerPosition,true)
                                return
                            }
                        materialButtonHorizontalAdapter.nextPrev(3)
                    }
                    4 -> {

                         if(!Subscription.isSubscribe){
                            gotoFrag(R.id.action_global_subscriptionFragment)
                            _viewPager.setCurrentItem(viewPagerPosition,true)
                            return
                        }

                        materialButtonHorizontalAdapter.nextPrev(4)
                    }
                }

                viewPagerPosition = position

                materialButtonHorizontalAdapter.notifyDataSetChanged()
                header.smoothScrollToPosition(viewPagerPosition)
            }
        })
    }

    override fun materialButtonHorizontalListClicked(absoluteAdapterPosition: Int) {
        super.materialButtonHorizontalListClicked(absoluteAdapterPosition)

        if (absoluteAdapterPosition == 0){
            _viewPager.currentItem = 0
        } else if (absoluteAdapterPosition == 1){
            _viewPager.currentItem = 1
        } else if (absoluteAdapterPosition == 2){
            _viewPager.currentItem = 2
        } else if (absoluteAdapterPosition == 3){
             if(!Subscription.isSubscribe){
                gotoFrag(R.id.action_global_subscriptionFragment)
                materialButtonHorizontalAdapter.nextPrev(viewPagerPosition)
                return
            }
            _viewPager.currentItem = 3
        } else if (absoluteAdapterPosition == 4){
             if(!Subscription.isSubscribe){
                gotoFrag(R.id.action_global_subscriptionFragment)
                materialButtonHorizontalAdapter.nextPrev(viewPagerPosition)
                return
            }
            _viewPager.currentItem = 4
        }

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

        cautionText.text = "ইসলামিক এই সকল বইগুলো যা দ্বীন অ্যাপে রয়েছে তার পিডিএফ অনলাইনে সহজলভ্য আছে। এই বইগুলার প্রকশক দ্বীন অ্যাপ নয়। আপনাদের পড়ার ও জানার সুবিধার জন্য আমরা সব বইগুলোকে একত্রিত করেছি। পাঠক হিসেবে আপনি বইগুলো পড়তে পারবেন। \n" +
                "\n" +
                "এই বইগুলো পড়ে আপনার ভালো লাগলে আপনি বইগুলো কিনে আপনার সংগ্রহে রাখতে পারেন। এতে আপনি, প্রকাশক ও লেখক সকলেই উপকৃত হবেন। ইনশাআল্লাহ! \n" +
                "\n" +
                "আলহামদুলিল্লাহ, এইটি সম্পূর্ণ ফ্রি ব্যবহারকারীর জন্য। এখান থেকে কোন মুনাফা দ্বীন অ্যাপ গ্রহন করেনা। যদি কোন প্রকাশক অথবা লেখক আপনাদের ইসলামিক বইগুলো আমাদের অ্যাপে পান এবং এইটাকে আপনার ব্যবসায়িক ক্ষতি বলে মনে করেন, তবে এখনি আমাদের ইমেল করুন (support@deenislamic.com) এ। আপনার পরিচয় যাচাই করে সঠিক বিবেচিত হলে আমরা ২৪ ঘন্টার মধ্যে আপনার বইগুলো সরিয়ে দিবো। ইনশাআল্লাহ!"

        val buttonOk: MaterialButton = view.findViewById(R.id.btn_ok)

        buttonOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(true)

        dialog.setContentView(view)

        dialog.show()
    }

}