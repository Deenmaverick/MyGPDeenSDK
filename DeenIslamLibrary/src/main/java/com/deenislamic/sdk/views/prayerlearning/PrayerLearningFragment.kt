package com.deenislamic.sdk.views.prayerlearning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PrayerLearningResource
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.repository.PrayerLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.MENU_PRAYER_LEARNING
import com.deenislamic.sdk.viewmodels.PrayerLearningViewModel
import com.deenislamic.sdk.views.adapters.prayerlearning.PrayerLearningCatAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class PrayerLearningFragment : BaseRegularFragment(),
    HorizontalCardListCallback {

    private lateinit var catRC:RecyclerView
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
    ): View {

        CallBackProvider.setFragment(this)

        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_prayer_learning,container,false)

        catRC = mainView.findViewById(R.id.catRC)
        setupActionForOtherFragment(0,0,null,localContext.getString(R.string.prayer_learning),true,mainView)
        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()

        if(!firstload)
            loadApi()
        firstload = true
    }


    private fun loadApi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getAllCat(getLanguage())
        }
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

    override fun noInternetRetryClicked() {
        loadApi()
    }

    override fun patchItemClicked(getData: Item) {
        when(getData.ContentType){

            "pldp" -> {
                 val bundle = Bundle()
                 bundle.putString("categoryID", getData.Text)
                 bundle.putString("pageTitle",getData.ArabicText)
                 bundle.putString("pageTag", MENU_PRAYER_LEARNING)

                 gotoFrag(R.id.action_global_subCatPatchFragment,bundle)
            }

            "pldd" -> {
                val bundle = Bundle()
                bundle.putString("categoryID", getData.Text)
                bundle.putString("pageTitle",getData.ArabicText)
                bundle.putString("pageTag", MENU_PRAYER_LEARNING)
                bundle.putString("contentType", getData.ContentType)
                bundle.putInt("subid", getData.SurahId)
                gotoFrag(R.id.action_global_subCatPatchFragment,bundle)
            }

            "ib" ->{
                val bundle = Bundle()
                bundle.putInt("id", getData.SurahId)
                bundle.putString("videoType", "category")
                bundle.putString("title",getData.ArabicText)
                gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)

            }

            /*"ibook" -> {
                val bundle = Bundle()
                bundle.putInt("id", getData.SurahId)
                bundle.putString("bookType", "category")
                bundle.putString("title",getData.ArabicText)
                gotoFrag(R.id.action_global_islamicBookPreviewFragment, bundle)

            }*/

            "prakat" -> {
                val bundle = Bundle()
                bundle.putString("title", getData.ArabicText)

                gotoFrag(R.id.action_global_prayerRakatFragment,bundle)
            }

            "ptrack" ->{
                gotoFrag(R.id.action_global_prayerTimesFragment)
            }

            "pvisual" ->{
                val bundle = Bundle()
                bundle.putString("title", getData.ArabicText)
                gotoFrag(R.id.action_global_prayerVisualFragment,bundle)
            }
        }
    }

}