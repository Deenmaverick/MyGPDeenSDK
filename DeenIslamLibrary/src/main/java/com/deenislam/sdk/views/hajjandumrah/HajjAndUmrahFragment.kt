package com.deenislam.sdk.views.hajjandumrah

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.HajjAndUmrahResource
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.service.repository.HajjAndUmrahRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.HAJJ_GUIDE
import com.deenislam.sdk.utils.HAJJ_SUB_CAT
import com.deenislam.sdk.viewmodels.HajjAndUmrahViewModel
import com.deenislam.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislam.sdk.views.adapters.hajjandumrah.HajjAndUmrahHomePatchAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch

internal class HajjAndUmrahFragment : BaseRegularFragment(), MenuCallback {

    private lateinit var listView:RecyclerView
    private lateinit var viewmodel: HajjAndUmrahViewModel
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
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
            backEnable = false,
            view = mainview
        )

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CallBackProvider.setFragment(this)

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
            //HAJJ_MAP -> gotoFrag(R.id.action_global_hajjMapFragment)
        }
    }


}