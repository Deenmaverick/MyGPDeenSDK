package com.deenislamic.sdk.views.common.subcatcardlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.BasicCardListCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.SubCatCardListResource
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.service.repository.SubCatCardListRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.viewmodels.SubCatCardListViewModel
import com.deenislamic.sdk.views.adapters.common.BasicCardListAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.gson.Gson
import kotlinx.coroutines.launch


internal class SubCatCardListFragment : BaseRegularFragment(), BasicCardListCallback {

    private val navArgs: SubCatCardListFragmentArgs by navArgs()
    private lateinit var viewmodel: SubCatCardListViewModel
    private lateinit var basicCardListAdapter: BasicCardListAdapter
    private lateinit var listView: RecyclerView
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        // init viewmodel
        val repository = SubCatCardListRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = SubCatCardListViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_sub_cat_card_list,container,false)

        listView = mainview.findViewById(R.id.listView)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = navArgs.pageTitle,
            backEnable = true,
            view = mainview
        )
        CallBackProvider.setFragment(this)
        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupBackPressCallback(this)

       /* if(firstload) {
            loadpage()
        }
        else if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()

        firstload = true
    }

    private fun loadpage()
    {
        initObserver()
        loadApi()
    }

    override fun noInternetRetryClicked() {
        super.noInternetRetryClicked()
        loadApi()
    }

    private fun loadApi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getSubCatList(navArgs.categoryID,getLanguage(),navArgs.pageTag)
        }
    }

    private fun initObserver()
    {
        viewmodel.subCatLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is SubCatCardListResource.SubcatList -> viewState(it.data)
            }
        }
    }

    private fun viewState(data: List<Data>)
    {

        Log.e("viewStateUM", Gson().toJson(data))

        basicCardListAdapter = BasicCardListAdapter()
        basicCardListAdapter.update(data)

        listView.apply {
            layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
            adapter = basicCardListAdapter
        }

        baseViewState()

    }

    override fun basicCardListItemSelect(data: Data) {

        Log.e("basicCardListItemSelect",Gson().toJson(data))
        val bundle = Bundle()
        bundle.putParcelable("data",data)
        bundle.putBoolean("shareable",navArgs.shareable)

        gotoFrag(R.id.action_global_subCatBasicCardDetailsFragment,bundle)
    }
}