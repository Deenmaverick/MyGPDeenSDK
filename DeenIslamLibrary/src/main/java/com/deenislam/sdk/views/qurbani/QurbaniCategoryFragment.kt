package com.deenislam.sdk.views.qurbani

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.QurbaniCallback
import com.deenislam.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.QurbaniResource
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.QurbaniViewModel
import com.deenislam.sdk.views.adapters.qurbani.QurbaniCategoryAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislamic.BaseApplication
import com.deenislamic.R
import com.deenislamic.service.callback.QurbaniCallback
import com.deenislamic.service.callback.common.HorizontalCardListCallback
import com.deenislamic.service.models.IslamicBookResource
import com.deenislamic.service.models.common.CommonResource
import com.deenislamic.service.models.qurbani.QurbaniResource
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislamic.utils.singleton.CallBackProvider
import com.deenislamic.utils.singleton.Subscription
import com.deenislamic.utils.transformDashboardItemForKhatamQuran
import com.deenislamic.viewmodels.IslamicBookViewModel
import com.deenislamic.viewmodels.QurbaniViewModel
import com.deenislamic.views.adapters.qurbani.QurbaniCategoryAdapter
import com.deenislamic.views.base.BaseRegularFragment
import com.deenislamic.views.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


internal class QurbaniCategoryFragment : BaseRegularFragment(), QurbaniCallback,
    HorizontalCardListCallback {

    private lateinit var listView:RecyclerView
    private lateinit var viewmodel: QurbaniViewModel
    private var firstload = false
    private lateinit var qurbaniCategoryAdapter: QurbaniCategoryAdapter
    private lateinit var islamicBookViewmodel:IslamicBookViewModel
    private var patchDataList: List<com.deenislam.sdk.service.network.response.dashboard.Data> ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_qurbani_category,container,false)

        listView = mainview.findViewById(R.id.listView)
        setupCommonLayout(mainview)

        CallBackProvider.setFragment(this)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()

        if(!firstload)
            loadapi()
        firstload = true
    }

    private fun loadapi(){
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getQurbaniPatch(getLanguage())
        }
    }

    private fun initObserver(){
         viewmodel.qurbaniLiveData.observe(viewLifecycleOwner){
             when(it){
                 is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                 is CommonResource.EMPTY -> baseEmptyState()
                 is QurbaniResource.QurbaniPatch -> {

                     patchDataList = it.data
                     if (!this::qurbaniCategoryAdapter.isInitialized)
                         qurbaniCategoryAdapter = QurbaniCategoryAdapter(it.data)

                     listView.apply {
                         adapter = qurbaniCategoryAdapter
                         baseViewState()
                     }
                 }
             }
         }

        islamicBookViewmodel.secureUrlLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is IslamicBookResource.PdfSecureUrl -> {

                    val bundle = Bundle()
                    bundle.putString("pageTitle", it.bookTitle)
                    bundle.putString("pdfUrl", it.url)
                    gotoFrag(R.id.action_global_pdfViewerFragment, bundle)

                }
            }
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    override fun selectedQurbaniCat(menuData: Item) {
        when(menuData.ContentType){

            "qic" ->{
                val bundle = Bundle()
                bundle.putParcelable("data",menuData)
                gotoFrag(R.id.action_global_qurbaniDetailsFragment,bundle)
            }

            "qh" -> {
                val bundle = Bundle()
                bundle.putString("pageTitle",menuData.ArabicText)
                bundle.putString("query","cattle+market")
                gotoFrag(R.id.action_global_nearestMosqueWebviewFragment,bundle)
            }

            "qoh" -> {
                val bundle = Bundle()
                bundle.putParcelable("data",menuData)
                gotoFrag(R.id.action_global_qurbaniOnlineHaatFragment,bundle)
            }

            "ibook" -> {
                    val bundle = Bundle()
                    bundle.putInt("id", menuData.SurahId)
                    bundle.putString("bookType", "category")
                    bundle.putString("title",menuData.ArabicText)
                    gotoFrag(R.id.action_global_islamicBookPreviewFragment, bundle)
            }

            "qsc" ->{
                val bundle = Bundle()
                bundle.putParcelable("data",menuData)
                bundle.putString("pageTitle",menuData.ArabicText)
                gotoFrag(R.id.action_global_qurbaniCommonContentFragment,bundle)
            }

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

            "ibook" -> {

                lifecycleScope.launch {
                    islamicBookViewmodel.getDigitalQuranSecureUrl(getData.imageurl2, false, getData.CategoryId, getData.ArabicText)
                }
            }

            "khq" -> {

                if (!BaseApplication.checkUserLoginStatus()) {
                    MainActivity.instance?.askUserLogin()
                    return
                }else if(!Subscription.isSubscribe){
                    gotoFrag(R.id.action_global_subscriptionFragment)
                    return
                }


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

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            islamicBookViewmodel.clearDownloader()
        }
    }

}