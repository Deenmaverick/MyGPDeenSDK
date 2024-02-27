package com.deenislam.sdk.views.prayerlearning

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.PrayerLearningCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.PrayerLearningResource
import com.deenislam.sdk.service.network.response.prayerlearning.Data
import com.deenislam.sdk.service.repository.PrayerLearningRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.MENU_PRAYER_LEARNING
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.viewmodels.PrayerLearningViewModel
import com.deenislam.sdk.views.adapters.prayerlearning.PrayerLearningCatAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch


internal class PrayerLearningFragment : BaseRegularFragment(), PrayerLearningCallback {

    private lateinit var catRC:RecyclerView
    private lateinit var menLayout:MaterialCardView
    private lateinit var womenLayout:MaterialCardView
    private lateinit var prayerLearningCatAdapter: PrayerLearningCatAdapter
    private lateinit var viewmodel: PrayerLearningViewModel
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
        // init viewmodel
        val repository = PrayerLearningRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = PrayerLearningViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        CallBackProvider.setFragment(this)

        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_prayer_learning,container,false)

        catRC = mainView.findViewById(R.id.catRC)
        menLayout = mainView.findViewById(R.id.menLayout)
        womenLayout = mainView.findViewById(R.id.womenLayout)
        setupActionForOtherFragment(0,0,null,localContext.getString(R.string.prayer_learning),true,mainView)
        setupCommonLayout(mainView)
        return mainView
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
                    pagename = "prayer_learning",
                    trackingID = getTrackingID()
                )
            }
        }


        /*if(firstload) {
            loadpage()
        }
        else if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()
    }

    private fun loadpage()
    {
        menLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("gender","male")
            bundle.putString("pageTitle",localContext.getString(R.string.prayer_visual_for_men))
            gotoFrag(R.id.action_global_prayerLearningDetailsFragment,bundle)
        }

        womenLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("gender","female")
            bundle.putString("pageTitle",localContext.getString(R.string.prayer_visual_for_women))
            gotoFrag(R.id.action_global_prayerLearningDetailsFragment,bundle)
        }

        initObserver()

        baseLoadingState()

        prayerLearningCatAdapter =  PrayerLearningCatAdapter()
        catRC.apply {

            adapter = prayerLearningCatAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
        }

        if(!firstload)
            loadApi()
        firstload = true
    }

    private fun loadApi()
    {
        lifecycleScope.launch {
            viewmodel.getAllCat(getLanguage())
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "prayer_learning",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun initObserver()
    {
        viewmodel.prayerLearningCatLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is PrayerLearningResource.AllCat -> viewState(it.data)
            }
        }
    }


    private fun viewState(data: List<Data>)
    {

        prayerLearningCatAdapter.update(data)
        catRC.post {
            baseViewState()
        }
    }

    override fun noInternetRetryClicked() {
        loadApi()
    }

    override fun catClicked(data: Data)
    {
        Log.e("catClicked","OK")
        val bundle = Bundle()
        bundle.putInt("categoryID", data.Id)
        bundle.putString("pageTitle",data.Category)
        bundle.putString("pageTag", MENU_PRAYER_LEARNING)
        bundle.putBoolean("shareable",true)

        gotoFrag(R.id.action_global_subCatCardListFragment,bundle)
    }

}