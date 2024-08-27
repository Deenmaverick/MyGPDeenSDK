package com.deenislamic.sdk.views.quran.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.QuranLearningCallback
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.quran.learning.QuranLearningViewModel
import com.deenislamic.sdk.views.adapters.quran.learning.QuranLearningHomePatch
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class QuranLearningFragment : BaseRegularFragment(),
    QuranLearningCallback,
    ViewInflationListener,
    HorizontalCardListCallback
{

    private lateinit var listMain:RecyclerView
    private var firstload = false
    private lateinit var viewmodel: QuranLearningViewModel

    override fun OnCreate() {
        super.OnCreate()

        setupBackPressCallback(this,true)

        viewmodel =QuranLearningViewModel(
            QuranLearningRepository(
                quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
                deenService = NetworkProvider().getInstance().provideDeenService(),
                dashboardService = NetworkProvider().getInstance().provideDashboardService()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_quran_learning,container,false)

        listMain = mainview.findViewById(R.id.listMain)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.quran_learning),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        CallBackProvider.setFragment(this)

        return mainview
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
                    pagename = "quran_class",
                    trackingID = getTrackingID()
                )
            }
        }


        /*if(firstload)
            loadPage()
        else if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else*/
            loadPage()


    }

    private fun loadPage()
    {

        initObserver()

        if(!firstload)
            loadapi()

    }

    private fun initObserver()
    {
        lifecycleScope.launch {
            viewmodel.quranLearningLiveData.observe(viewLifecycleOwner)
            {
                when(it)
                {
                    CommonResource.API_CALL_FAILED -> baseNoInternetState()
                    is QuranLearningResource.HomePatch ->{

                        listMain.apply {
                            adapter = QuranLearningHomePatch(it.data)
                        }

                    }
                }
            }
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "quran_class",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun loadapi(){
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getHomePatch(getLanguage())
        }
    }

    override fun homePatchItemClicked(getData: Item) {

        when(getData.ContentType)
        {
            "dqc" -> {
                val bundle = Bundle()
                bundle.putString("title",getData.MText)
                bundle.putInt("courseId",getData.SurahId)

                gotoFrag(R.id.action_global_quranLearningDetailsFragment,bundle)
            }

            "qsa" -> gotoFrag(R.id.action_global_quranLearningTpFragment)
        }

    }

    override fun onAllViewsInflated() {
       listMain.post {
           baseViewState()
           firstload = true
       }
    }

    override fun patchItemClicked(getData: Item) {


        when(getData.ContentType)
        {
            "dqc" -> {
                val bundle = Bundle()
                bundle.putString("title",getData.MText)
                bundle.putInt("courseId",getData.Id)

                gotoFrag(R.id.action_global_quranLearningDetailsFragment,bundle)
            }

            "qsa" -> gotoFrag(R.id.action_global_quranLearningTpFragment)
        }
    }
}