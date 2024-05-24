package com.deenislam.sdk.views.qurbani

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.QurbaniCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.QurbaniResource
import com.deenislam.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislam.sdk.service.repository.QurbaniRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.QurbaniViewModel
import com.deenislam.sdk.views.adapters.qurbani.QurbaniOnlineHaatAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


internal class QurbaniOnlineHaatFragment : BaseRegularFragment(), QurbaniCallback {

    private lateinit var listview:RecyclerView
    private lateinit var viewModel: QurbaniViewModel
    private lateinit var qurbaniOnlineHaatAdapter: QurbaniOnlineHaatAdapter
    private var firstload = false
    private val navArgs:QurbaniOnlineHaatFragmentArgs by navArgs()

    override fun OnCreate() {
        super.OnCreate()

        val repository = QurbaniRepository(
            deenService = NetworkProvider().getInstance().provideDeenService()
        )

        viewModel = QurbaniViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_qurbani_online_haat,container,false)

        listview = mainview.findViewById(R.id.listview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.online_haat),
            backEnable = true,
            view = mainview
        )

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

    private fun loadapi() {
        baseLoadingState()
        lifecycleScope.launch {
            viewModel.getContentByCat(navArgs.data.SurahId,getLanguage())
        }

    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun initObserver(){
        viewModel.qurbaniLiveData.observe(viewLifecycleOwner){
            when(it){
                is CommonResource.EMPTY -> baseEmptyState()
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is QurbaniResource.QurbaniContentByCat -> {

                    if(!this::qurbaniOnlineHaatAdapter.isInitialized)
                        qurbaniOnlineHaatAdapter = QurbaniOnlineHaatAdapter(it.data)

                    listview.apply {
                        layoutManager = GridLayoutManager(context,2)
                        adapter = qurbaniOnlineHaatAdapter
                    }
                    baseViewState()
                }
            }
        }
    }

    override fun qurbaniHaatDirection(getdata: Data) {

        val bundle = Bundle()
        bundle.putString("title", Jsoup.parse(getdata.Text.toString()).text())
        bundle.putString("weburl", Jsoup.parse(getdata.reference.toString()).text())
        gotoFrag(R.id.action_global_basicWebViewFragment, bundle)

    }

}