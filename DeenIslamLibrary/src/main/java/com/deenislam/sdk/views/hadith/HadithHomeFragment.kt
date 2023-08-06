package com.deenislam.sdk.views.hadith

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.HadithResource
import com.deenislam.sdk.service.network.response.hadith.Data
import com.deenislam.sdk.service.repository.HadithRepository
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.HadithViewModel
import com.deenislam.sdk.views.adapters.hadith.HadithCollectionAdapter
import com.deenislam.sdk.views.adapters.hadith.HadithCollectionCallback
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

internal class HadithHomeFragment : BaseRegularFragment(), HadithCollectionCallback {

    private lateinit var listView: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton
    private lateinit var hadithCollectionAdapter: HadithCollectionAdapter

    private var firstload:Boolean = false

    private lateinit var viewModel: HadithViewModel

    override fun OnCreate() {
        super.OnCreate()

        // init viewmodel
        val repository = HadithRepository(
            hadithService = NetworkProvider().getInstance().provideHadithService()
        )
        viewModel = HadithViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_hadith_home,container,false)

        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)


        //click retry button for get api data again
        noInternetRetry.setOnClickListener {
            loadApiData()
        }

        hadithCollectionAdapter = HadithCollectionAdapter(this@HadithHomeFragment)

        listView.apply {

            val margins = (layoutParams as ConstraintLayout.LayoutParams).apply {
                leftMargin = 12.dp
                rightMargin = 12.dp
            }
            layoutParams = margins

            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = hadithCollectionAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        initObserver()

        if(!firstload)
            loadApiData()

        firstload = true

    }

    private fun loadApiData()
    {
        loadingState()
        lifecycleScope.launch {
            viewModel.getHadithCollection(language = "en")
        }
    }

    private fun initObserver()
    {
        viewModel.hadithLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()
                CommonResource.EMPTY -> emptyState()
                is HadithResource.hadithCollection -> viewState(it.data)

            }
        }

    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        noInternetLayout.visible(false)
    }

    private fun emptyState()
    {
        progressLayout.hide()
        nodataLayout.show()
        noInternetLayout.hide()
    }

    private fun noInternetState()
    {
        progressLayout.hide()
        nodataLayout.hide()
        noInternetLayout.show()
    }

    private fun viewState(data: List<Data>)
    {
        hadithCollectionAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun CollectionClicked(name: String, title: String) {
        val bundle = Bundle().apply {
            putString("collectionName", name)
            putString("pageTitile", title)
        }
        gotoFrag(R.id.action_hadithFragment_to_hadithChapterFragment,data = bundle)
    }
}