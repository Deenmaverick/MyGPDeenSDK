package com.deenislamic.sdk.views.dailydua

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.DashboardPatchCallback
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.callback.quran.QuranPlayerCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.ImageViewPopupDialog
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.DailyDuaResource
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.repository.DailyDuaRepository
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.RECYCLERFOOTER
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.transformDashboardItemForKhatamQuran
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.DailyDuaViewModel
import com.deenislamic.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislamic.sdk.views.adapters.dailydua.AllDuaAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

internal class AllDuaFragment : BaseRegularFragment(),
    MenuCallback,
    HorizontalCardListCallback,
    DashboardPatchCallback,
    QuranPlayerCallback {

    private lateinit var listView:RecyclerView
    private lateinit var progressLayout:LinearLayout
    private lateinit var nodataLayout:NestedScrollView
    private lateinit var noInternetLayout:NestedScrollView
    private lateinit var noInternetRetry:MaterialButton
    private lateinit var allDuaAdapter: AllDuaAdapter

    private lateinit var viewmodel: DailyDuaViewModel

    private var firstload:Boolean = false
    private var patchDataList: List<com.deenislamic.sdk.service.network.response.dashboard.Data> ? = null


    override fun OnCreate() {
        super.OnCreate()

         // init viewmodel
        val repository = DailyDuaRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = DailyDuaViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_daily_dua_page,container,false)

        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)

        if(firstload) {
            progressLayout.hide()
        }
        CallBackProvider.setFragment(this)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadpage()
    }

    private fun loadpage()
    {

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)


        //click retry button for get api data again
        noInternetRetry.setOnClickListener {
            loadApiData()
        }

        allDuaAdapter = AllDuaAdapter()

        val gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (allDuaAdapter.getItemViewType(position)) {
                    RECYCLERFOOTER -> 3
                    else -> 1
                }
            }
        }

        listView.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter = allDuaAdapter

                //layoutManager = gridLayoutManager

        }

        initObserver()

        if(!firstload)
            loadApiData()
        firstload = true

    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "daily_dua",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }


    private fun loadApiData()
    {
        loadingState()
        lifecycleScope.launch {
            viewmodel.getDuaAllCategory(getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.allDayLiveData.observe(viewLifecycleOwner)
        {
            Log.e("allDayLiveData","called")
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()
                CommonResource.EMPTY -> emptyState()
                is DailyDuaResource.duaALlCategory -> viewState(it.data)
            }
        }
    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        noInternetLayout.visible(false)
    }

    private fun emptyState()
    {
        progressLayout.hide()
        nodataLayout.show()
        noInternetLayout.hide()
    }

    private fun noInternetState()
    {
        progressLayout.hide()
        nodataLayout.hide()
        noInternetLayout.show()
    }

    private fun viewState(data: List<com.deenislamic.sdk.service.network.response.dashboard.Data>)
    {
        allDuaAdapter.update(data)
        patchDataList = data
        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

   /* override fun selectedCat(id: Int, category: String) {

        val bundle = Bundle().apply {
            putInt("category", id)
            putString("catName", category)
        }
        gotoFrag(R.id.action_global_allDuaPreviewFragment,data = bundle)
    }*/

    override fun patchItemClicked(getData: Item) {

        when(getData.ContentType){

            "ib" -> {
                val bundle = Bundle()
                bundle.putInt("id", getData.CategoryId)
                bundle.putString("videoType", "category")
                bundle.putString("pageTitle",getData.ArabicText)
                gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
            }

            /*"ibook" -> {

                lifecycleScope.launch {
                    islamicBookViewmodel.getDigitalQuranSecureUrl(getData.imageurl2, false, getData.CategoryId, getData.ArabicText)
                }
            }*/

            "khq" -> {



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

            "dtub" -> {

                patchDataList?.let {

                    var dataIndex = -1
                    var itemIndex = 0

                    it.forEachIndexed { index, dataValue ->
                        dataValue.Items.forEachIndexed { innerIndex, item ->
                            // Replace with the condition to check if the items match
                            if (item.ContentType == getData.ContentType && item.Id == getData.Id) {
                                dataIndex = index
                                itemIndex = innerIndex
                                return@forEachIndexed
                            }
                        }
                    }

                    if(dataIndex !=-1) {
                        val bundle = Bundle()
                        bundle.putParcelable("selectedData", getData)
                        bundle.putParcelableArray("data", it[dataIndex].Items.toTypedArray())
                        gotoFrag(R.id.action_global_youtubeVideoFragment, bundle)
                    }

                }
            }
        }

    }

    override fun dashboardPatchClickd(patch: String, data: Item?) {
        when(patch){
            "dua" -> {
                //changeMainViewPager(2)

                data?.let {
                    val bundle = Bundle()
                    bundle.putString("title",it.FeatureTitle)
                    bundle.putString("imgUrl","$BASE_CONTENT_URL_SGP${it.imageurl1}")
                    //bundle.putString("content","${getdata.Title}:\n\n${getdata.Text}\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")
                    ImageViewPopupDialog.display(childFragmentManager,bundle)

                }
            }
        }
    }

    override fun menuClicked(pagetag: String, getMenu: Item?) {
        getMenu?.let {
            val bundle = Bundle().apply {
                putInt("category", it.SurahId)
                putString("catName", it.ArabicText)
            }
            gotoFrag(R.id.action_global_allDuaPreviewFragment,data = bundle)
        }
    }
}