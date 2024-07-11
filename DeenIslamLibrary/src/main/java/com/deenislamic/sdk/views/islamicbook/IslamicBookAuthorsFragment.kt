package com.deenislamic.sdk.views.islamicbook

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicBookResource
import com.deenislamic.sdk.service.network.response.boyan.scholarspaging.Data
import com.deenislamic.sdk.service.repository.IslamicBookRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.viewmodels.IslamicBookViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.islamicboyan.adapter.BoyanScholarCallback
import com.deenislamic.sdk.views.islamicboyan.adapter.BoyanScholarsPagindAdapter
import kotlinx.coroutines.launch


internal class IslamicBookAuthorsFragment : BaseRegularFragment(), BoyanScholarCallback {

    private lateinit var listView: RecyclerView
    private lateinit var boyanScholarsPagingAdapter: BoyanScholarsPagindAdapter

    private lateinit var viewModel: IslamicBookViewModel

    private var firstload: Boolean = false

    override fun OnCreate() {
        super.OnCreate()

        val repository = IslamicBookRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())

        val quranLearningRepository = QuranLearningRepository(
            quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
            deenService = NetworkProvider().getInstance().provideDeenService(),
            dashboardService = NetworkProvider().getInstance().provideDashboardService()
        )

        viewModel = IslamicBookViewModel(repository = repository, quranLearningRepository = quranLearningRepository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_book_authors, container, false)

        //init view
        listView = mainView.findViewById(R.id.listView)

        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boyanScholarsPagingAdapter = BoyanScholarsPagindAdapter(this@IslamicBookAuthorsFragment, "book")

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
            viewModel.getBookAuthors(language = getLanguage(), page = 1, limit = 100)
        }
    }

    private fun initObserver() {
        viewModel.bookAuthirsLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is IslamicBookResource.BookAuthorsData -> {

                    Log.d("TheDataDataDataScholar", "data: ")
                    //Toast.makeText(requireContext(), "data: "+it.data, Toast.LENGTH_SHORT).show()

                    listView.apply {
                        adapter = boyanScholarsPagingAdapter
                    }

                    boyanScholarsPagingAdapter.update(it.data)

                    Log.d("TheAuthorDataTestTest", it.data.toString())

                    baseViewState()
                }

            }
        }
    }

    override fun scholarClick(data: Data) {
        val bundle = Bundle()
        bundle.putInt("id", data.Id)
        bundle.putString("bookType", "author")
        bundle.putString("title", data.Name)
        gotoFrag(R.id.action_global_islamicBookPreviewFragment, bundle)
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