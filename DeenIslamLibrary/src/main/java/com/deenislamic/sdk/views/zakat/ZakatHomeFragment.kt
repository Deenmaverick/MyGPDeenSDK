package com.deenislamic.sdk.views.zakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicBookResource
import com.deenislamic.sdk.service.models.ZakatResource
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.repository.IslamicBookRepository
import com.deenislamic.sdk.service.repository.ZakatRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.MENU_ZAKAT
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.IslamicBookViewModel
import com.deenislamic.sdk.viewmodels.ZakatViewModel
import com.deenislamic.sdk.views.adapters.prayerlearning.PrayerLearningCatAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch

internal class ZakatHomeFragment : BaseRegularFragment(), HorizontalCardListCallback {

    private lateinit var catRC: RecyclerView
    private lateinit var prayerLearningCatAdapter: PrayerLearningCatAdapter
    private lateinit var viewModel: ZakatViewModel
    private lateinit var islamicBookViewmodel: IslamicBookViewModel

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        // init voiewmodel
        val repository = ZakatRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewModel = ZakatViewModel(repository)


        val iBrepository = IslamicBookRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())

        val quranLearningRepository = QuranLearningRepository(
            quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
            deenService = NetworkProvider().getInstance().provideDeenService(),
            dashboardService = NetworkProvider().getInstance().provideDashboardService()
        )

        islamicBookViewmodel = IslamicBookViewModel(repository = iBrepository, quranLearningRepository = quranLearningRepository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_zakat_home,container,false)

        //init view
        catRC = mainView.findViewById(R.id.catRC)
        setupCommonLayout(mainView)
        CallBackProvider.setFragment(this)
        return mainView
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
            viewModel.getPatch(getLanguage())
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible){
            CallBackProvider.setFragment(this)
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun initObserver(){

        viewModel.zakatLiveData.observe(viewLifecycleOwner){
            when(it){
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is ZakatResource.Patch -> {
                    viewState(it.data)
                }
            }
        }

        islamicBookViewmodel.secureUrlLiveData.observe(viewLifecycleOwner) {
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

    private fun viewState(data: List<com.deenislamic.sdk.service.network.response.dashboard.Data>)
    {

        if(!this::prayerLearningCatAdapter.isInitialized)
            prayerLearningCatAdapter =  PrayerLearningCatAdapter(data)

        catRC.apply {
            adapter = prayerLearningCatAdapter
        }

        catRC.post {
            baseViewState()
        }
    }

    override fun patchItemClicked(getData: Item) {
        when(getData.ContentType){

            "pldp" -> {
                val bundle = Bundle()
                bundle.putInt("categoryID", getData.SurahId)
                bundle.putString("pageTitle",getData.ArabicText)
                bundle.putString("pageTag", MENU_ZAKAT)
                gotoFrag(R.id.action_global_subContentFragment,bundle)
            }


            "ib" ->{

                val bundle = Bundle()
                bundle.putInt("id", getData.CategoryId)
                bundle.putString("videoType", "category")
                bundle.putString("title",getData.ArabicText)
                gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)

            }

            "ibook" -> {

                if(getData.SurahId>0) {
                    val bundle = Bundle()
                    bundle.putInt("id", getData.SurahId)
                    bundle.putString("bookType", "category")
                    bundle.putString("title", getData.ArabicText)
                    gotoFrag(R.id.action_global_islamicBookPreviewFragment, bundle)
                }else{
                    lifecycleScope.launch {
                        islamicBookViewmodel.getDigitalQuranSecureUrl(getData.imageurl2, false, getData.CategoryId, getData.ArabicText)
                    }
                }

            }


            "cacl" -> {
                gotoFrag(R.id.action_zakatFragment_to_zakatCalculatorFragment)
            }

            "subs" -> {
                //gotoFrag(R.id.action_global_subscriptionNewFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            islamicBookViewmodel.clearDownloader()
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "zakat",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }

}