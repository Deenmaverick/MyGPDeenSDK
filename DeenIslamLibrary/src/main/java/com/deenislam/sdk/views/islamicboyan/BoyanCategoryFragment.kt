package com.deenislam.sdk.views.islamicboyan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.BoyanResource
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.repository.BoyanRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.BoyanViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.islamicboyan.adapter.BoyanCategoriesCallback
import com.deenislam.sdk.views.islamicboyan.adapter.BoyanCategoriesPagingAdapter
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

        boyanCategoriesPagingAdapter = BoyanCategoriesPagingAdapter(this@BoyanCategoryFragment)

        initObserver()

        if(firstload) {
            loadApiData()
        }
        else if (!isDetached) {
            view.postDelayed({
                loadApiData()
            }, 300)
        }
        else
            loadApiData()

        firstload = true

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

    override fun chapterClick(boyanId: Int) {
        val bundle = Bundle()
        bundle.putInt("id", boyanId)
        bundle.putString("videoType", "category")
        gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible){
            CallBackProvider.setFragment(this)
        }
    }
}