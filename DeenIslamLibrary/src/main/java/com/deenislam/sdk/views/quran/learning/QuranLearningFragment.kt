package com.deenislam.sdk.views.quran.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.QuranLearningCallback
import com.deenislam.sdk.service.callback.ViewInflationListener
import com.deenislam.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.quran.learning.QuranLearningViewModel
import com.deenislam.sdk.views.adapters.quran.learning.QuranLearningHomePatch
import com.deenislam.sdk.views.base.BaseRegularFragment
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

        if(firstload)
            loadPage()
        else if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else
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