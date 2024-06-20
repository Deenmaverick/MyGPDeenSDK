package com.deenislamic.sdk.views.islamicboyan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.BoyanResource
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.network.response.boyan.categoriespaging.Data
import com.deenislamic.sdk.service.repository.BoyanRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.viewmodels.BoyanViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.islamicboyan.adapter.BoyanCategoriesCallback
import com.deenislamic.sdk.views.islamicboyan.adapter.BoyanCategoriesPagingAdapter
import kotlinx.coroutines.launch


internal class BoyanCategoryFragment : BaseRegularFragment(), BoyanCategoriesCallback {

    private lateinit var listView: RecyclerView
    private lateinit var boyanCategoriesPagingAdapter: BoyanCategoriesPagingAdapter

    private lateinit var viewModel: BoyanViewModel

    private var firstload:Boolean = false

    override fun OnCreate() {
        super.OnCreate()
        val boyanRepository = BoyanRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewModel = BoyanViewModel(boyanRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_boyan_category, container, false)

        //Toast.makeText(requireContext(), "onCreateView", Toast.LENGTH_SHORT).show()

        //init view
        listView = mainView.findViewById(R.id.listView)

        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!this::boyanCategoriesPagingAdapter.isInitialized)
            boyanCategoriesPagingAdapter = BoyanCategoriesPagingAdapter(this@BoyanCategoryFragment)

        listView.apply {
            adapter = boyanCategoriesPagingAdapter
        }
        initObserver()

        baseLoadingState()

    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewModel.getBoyanCategories(language = getLanguage(), page = 1, limit = 100)
        }
    }

    private fun initObserver() {
        viewModel.boyanCategoryLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is BoyanResource.BoyanCategoryData -> {

                    listView.apply {
                        adapter = boyanCategoriesPagingAdapter
                    }

                    boyanCategoriesPagingAdapter.update(it.data)

                    baseViewState()
                }

            }
        }
    }

    override fun chapterClick(data: Data) {
        val bundle = Bundle()
        bundle.putInt("id", data.Id)
        bundle.putString("videoType", "category")
        bundle.putString("title",data.category)
        gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible){
            CallBackProvider.setFragment(this)
            if(!firstload) {
                loadApiData()
            }
            firstload = true
        }
    }
}