package com.deenislamic.sdk.views.hadith

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.HadithResource
import com.deenislamic.sdk.service.network.response.hadith.preview.Data
import com.deenislamic.sdk.service.repository.HadithRepository
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.HadithViewModel
import com.deenislamic.sdk.views.adapters.hadith.HadithPreviewAdapter
import com.deenislamic.sdk.views.adapters.hadith.HadithPreviewCallback
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch

internal class HadithPreviewFragment : BaseRegularFragment(),HadithPreviewCallback {

    private lateinit var listView: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton
    private lateinit var actionbar: ConstraintLayout
    private lateinit var hadithPreviewAdapter: HadithPreviewAdapter
    private lateinit var last_item_loading_progress:CircularProgressIndicator

    private val hadithData :ArrayList<Data> = arrayListOf()

    var previousScrollY = 0
    var isScrollAtEnd = false
    private var isNextEnabled  = true
    private var pageNo:Int = 1
    private var pageItemCount:Int = 10
    private var nextPageAPICalled:Boolean = false
    private var totalHadithCount = 0

    private lateinit var viewModel: HadithViewModel

    private val args:HadithPreviewFragmentArgs by navArgs()

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).apply {
            duration = 300L
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }

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
        val mainView = layoutInflater.inflate(R.layout.fragment_hadith_home,container,false)

        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)
        actionbar = mainView.findViewById(R.id.actionbar)
        last_item_loading_progress = mainView.findViewById(R.id.last_item_loading_progress)

        actionbar.show()

        setupActionForOtherFragment(0,0,null,args.title,true,mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPage()
        /*view.postDelayed({
            // Code to execute after the animation
            loadPage()
        }, 300)*/



    }

    private fun loadPage()
    {
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        //click retry button for get api data again
        noInternetRetry.setOnClickListener {
            loadingState()
            loadApiData()
        }

        hadithPreviewAdapter = HadithPreviewAdapter(this@HadithPreviewFragment)

        listView.apply {

            setPadding(0,12.dp,0,0)

            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = hadithPreviewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

        // List scroll listner for pagging

        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    // RecyclerView has reached the end.
                    // Load more data here if you are implementing pagination.
                    // NestedScrollView has scrolled to the end
                    if(isNextEnabled && hadithPreviewAdapter.itemCount>0) {
                        fetchNextPageData()
                        morePageBottomLoading(true)
                    }
                    else
                        morePageBottomLoading(false)
                }


            }
        })


        /* listView.viewTreeObserver.addOnScrollChangedListener {
             val scrollY = listView.scrollY
             val totalContentHeight = listView.getChildAt(0)?.let { it.measuredHeight - listView.height }

             if (scrollY >= (totalContentHeight ?: 0) && !isScrollAtEnd) {
                 isScrollAtEnd = true
                 // NestedScrollView has scrolled to the end
                 if(isNextEnabled && hadithPreviewAdapter.itemCount>0) {
                     nextPageAPICalled = true
                     fetchNextPageData()
                     Log.e("ALQURAN_SCROLL","END")
                     morePageBottomLoading(true)
                 }
                 else
                     morePageBottomLoading(false)
             }

         }*/

        initObserver()
        loadingState()
        loadApiData()
    }

    private fun morePageBottomLoading(bol:Boolean)
    {
        last_item_loading_progress.visible(bol)
    }

    private fun fetchNextPageData() {

        lifecycleScope.launch {
            pageNo++
            loadApiData()
            isNextEnabled = false
        }
    }

    private fun loadApiData(page:Int = pageNo)
    {
        lifecycleScope.launch {
            viewModel.getHadithPreview(language = getLanguage(), bookId = args.bookId,chapterId = args.chapterId, page = page, limit = pageItemCount)
        }
    }

    private fun initObserver()
    {
        viewModel.hadithPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()
                CommonResource.EMPTY -> emptyState()
                is HadithResource.hadithPreview -> {

                    morePageBottomLoading(false)

                    totalHadithCount =  it.value.TotalData

                    Log.e("totalHadithCount",totalHadithCount.toString())

                    isNextEnabled = it.value.Pagination.isNext
                    it.value.Data?.let { it1 -> viewState(it1) }
                }
                is HadithResource.setFavHadith -> updateFavorite(it.position,it.fav)

            }
        }

    }

    private fun updateFavorite(position: Int, fav: Boolean)
    {
        hadithPreviewAdapter.update(position,fav)
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
        hadithPreviewAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun favcClick(isFavorite: Boolean, duaId: Int, position: Int) {
        if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }
        lifecycleScope.launch {
            viewModel.setFavHadith(isFavorite,duaId,getLanguage(),position)
        }
    }

}