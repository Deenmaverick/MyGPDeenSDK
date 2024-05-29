package com.deenislam.sdk.views.hajjandumrah

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.ImageViewPopupDialog
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.HajjAndUmrahResource
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.service.repository.HajjAndUmrahRepository
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.HAJJ_GUIDE
import com.deenislam.sdk.utils.HAJJ_SUB_CAT
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.transformDashboardItemForKhatamQuran
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.viewmodels.HajjAndUmrahViewModel
import com.deenislam.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislam.sdk.views.adapters.hajjandumrah.HajjAndUmrahHomePatchAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch

internal class HajjAndUmrahFragment : BaseRegularFragment(), MenuCallback,
    HorizontalCardListCallback, DashboardPatchCallback {

    private lateinit var listView:RecyclerView
    private lateinit var viewmodel: HajjAndUmrahViewModel
    private var firstload = false
    private var patchDataList: List<com.deenislam.sdk.service.network.response.dashboard.Data> ? = null


    override fun OnCreate() {
        super.OnCreate()

        setupBackPressCallback(this,true)


        val hajjAndUmrahRepository = HajjAndUmrahRepository(
            deenService = NetworkProvider().getInstance().provideDeenService()
        )

        viewmodel = HajjAndUmrahViewModel(repository = hajjAndUmrahRepository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_hajj_and_umrah,container,false)

        listView = mainview.findViewById(R.id.listView)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.hajj_umrah),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CallBackProvider.setFragment(this)

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "hajjandumrah",
                    trackingID = getTrackingID()
                )
            }
        }

        initObserver()

        if(!firstload)
        loadApi()
        firstload = true
    }


    private fun loadApi()
    {

        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getHajjAndUmrahPatch(getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.hajjAndUmrahPatchLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is HajjAndUmrahResource.hajjAndUmrahPatch -> viewState(it.data)
            }
        }

    }

    private fun viewState(data: List<com.deenislam.sdk.service.network.response.dashboard.Data>)
    {

        patchDataList = data

        listView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = HajjAndUmrahHomePatchAdapter(data)
        }
        baseViewState()
    }


    override fun noInternetRetryClicked() {
        super.noInternetRetryClicked()
        loadApi()
    }

    override fun menuClicked(pagetag: String, getMenu: Item?) {

        when(pagetag)
        {
            HAJJ_SUB_CAT -> {
                val bundle = Bundle()
                getMenu?.let {
                    bundle.putInt("categoryID", it.Reference.toInt())
                    bundle.putString("pageTitle",it.ArabicText)
                    bundle.putString("pageTag",pagetag)
                    bundle.putBoolean("shareable",true)
                    gotoFrag(R.id.action_global_subCatCardListFragment,bundle)
                }
            }

            HAJJ_GUIDE -> {
                val bundle = Bundle()
                getMenu?.let {
                    bundle.putInt("categoryId", it.Reference.toInt())
                    gotoFrag(R.id.action_global_hajjGuideFragment,bundle)
                }
            }

            "dua" -> {

                getMenu?.SurahId?.let {
                    val bundle = Bundle().apply {
                        putInt("category", it)
                        putString("catName", getMenu.ArabicText)
                    }
                    gotoFrag(R.id.action_global_allDuaPreviewFragment,data = bundle)
                }


            }
            //HAJJ_MAP -> gotoFrag(R.id.action_global_hajjMapFragment)
        }
    }


    override fun patchItemClicked(getData: Item) {

        when(getData.ContentType){

            "ib" -> {
                val bundle = Bundle()
                bundle.putInt("id", getData.CategoryId)
                bundle.putString("videoType", "category")
                bundle.putString("pageTitle",getData.ArabicText)
                gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
            }


            "khq" -> {

              /*  if(!Subscription.isSubscribe){
                    gotoFrag(R.id.action_global_subscriptionFragment)
                    return
                }*/


                patchDataList?.let {

                    var dataIndex = -1
                    var itemIndex = 0

                    patchDataList?.forEachIndexed { index, data ->
                        data.Items.forEachIndexed { innerIndex, item ->
                            // Replace with the condition to check if the items match
                            if (item.Id == getData.Id) {
                                dataIndex = index
                                itemIndex = innerIndex
                                return@forEachIndexed
                            }
                        }
                    }

                    if(dataIndex !=-1) {
                        val bundle = Bundle()
                        bundle.putInt("khatamQuranvideoPosition", itemIndex)
                        bundle.putParcelableArray("khatamQuranvideoList", it[dataIndex].Items.map { it1-> transformDashboardItemForKhatamQuran(it1) }.toTypedArray())
                        gotoFrag(R.id.action_global_khatamEQuranVideoFragment, bundle)
                    }

                }

            }



        }

    }

    override fun dashboardPatchClickd(patch: String, data: Item?) {

        when(patch){
            "dua" -> {

                val bundle = Bundle()
                bundle.putString("title", localContext.getString(R.string.dua))
                bundle.putString("imgUrl", "$BASE_CONTENT_URL_SGP${data?.imageurl1}")
                //bundle.putString("content","${getdata.Title}:\n\n${getdata.Text}\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")

                ImageViewPopupDialog.display(childFragmentManager, bundle)

            }
        }
    }

    override fun onBackPress() {

        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "hajjandumrah",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }
}