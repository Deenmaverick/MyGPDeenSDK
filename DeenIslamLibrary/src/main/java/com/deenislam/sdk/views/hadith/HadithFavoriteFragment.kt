package com.deenislam.sdk.views.hadith

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislam.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.HadithResource
import com.deenislam.sdk.service.network.response.hadith.preview.Data
import com.deenislam.sdk.service.repository.HadithRepository
import com.deenislam.sdk.utils.LoadingButton
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.HadithViewModel
import com.deenislam.sdk.views.adapters.hadith.HadithFavAdapter
import com.deenislam.sdk.views.adapters.hadith.hadithFavCallback
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch

internal class HadithFavoriteFragment : BaseRegularFragment(), CustomDialogCallback,
    hadithFavCallback {

    private lateinit var listView: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton
    private lateinit var last_item_loading_progress: CircularProgressIndicator

    private lateinit var hadithFavAdapter: HadithFavAdapter
    private val hadithData :ArrayList<Data> = arrayListOf()

    private var customAlertDialog: CustomAlertDialog? =null

    var isScrollAtEnd = false
    private var isNextEnabled  = true
    private var pageNo:Int = 1
    private var pageItemCount:Int = 30
    private var nextPageAPICalled:Boolean = false
    private var totalHadithCount = 0

    private var favData: Data? =null
    private var adapterPosition:Int = -1
    private lateinit var viewModel:HadithViewModel


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
        val mainView = localInflater.inflate(R.layout.fragment_hadith_favorite,container,false)
        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)
        last_item_loading_progress = mainView.findViewById(R.id.last_item_loading_progress)

        customAlertDialog = CustomAlertDialog().getInstance()
        customAlertDialog?.setupDialog(
            callback = this@HadithFavoriteFragment,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.cancel),
            btn2Text = localContext.getString(R.string.delete),
            titileText = localContext.getString(R.string.want_to_delete),
            subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
        )

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

        initObserver()

        loadingState()

        hadithFavAdapter = HadithFavAdapter(this@HadithFavoriteFragment)

        listView.apply {

            val margins = (layoutParams as ConstraintLayout.LayoutParams).apply {
                leftMargin = 12.dp
                rightMargin = 12.dp
            }
            layoutParams = margins

            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = hadithFavAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    // RecyclerView has reached the end.
                    // Load more data here if you are implementing pagination.
                    Log.e("RecyclerView_Listview", "Reached the end")
                    // NestedScrollView has scrolled to the end
                    if(isNextEnabled && hadithFavAdapter.itemCount>0) {
                        nextPageAPICalled = true
                        fetchNextPageData()
                        Log.e("ALQURAN_SCROLL","END")
                        morePageBottomLoading(true)
                    }
                    else
                        morePageBottomLoading(false)
                }


            }
        })

    }

    private fun fetchNextPageData() {

        Log.e("fetchNextPageData","called")
        val from = hadithFavAdapter.itemCount
        val to = hadithData.size

        if (totalHadithCount != hadithFavAdapter.itemCount && nextPageAPICalled) {
            lifecycleScope.launch {
                pageNo++
                loadApiData()
                nextPageAPICalled = false
            }
        }
        else
        {
            if (from == 0 && totalHadithCount <= pageItemCount) {
                hadithFavAdapter.update(ArrayList(hadithData))
                //alQuranAyatAdapter.notifyItemRangeInserted(from,to)
            }
            else if(hadithFavAdapter.itemCount != hadithData.size)
            {
                if(from<to) {
                    listView.setOnTouchListener { _, _ -> true }
                    listView.post {
                        hadithFavAdapter.update(ArrayList(hadithData.subList(from, to)))
                        listView.setOnTouchListener(null)
                    }
                    //alQuranAyatAdapter.notifyItemRangeInserted(from, to)
                }
            }


            listView.post {
                // binding.container.setScrollingEnabled(true)
                isScrollAtEnd = false
                nextPageAPICalled = false
            }
        }
    }

    private fun morePageBottomLoading(bol:Boolean)
    {
        last_item_loading_progress.visible(bol)
    }

    override fun onBackPress() {

        lifecycleScope.launch {
            userTrackViewModel.trackUser(
                language = getLanguage(),
                msisdn = Deen.msisdn,
                pagename = "hadith",
                trackingID = getTrackingID()
            )
        }
        super.onBackPress()
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

    override fun onResume() {
        super.onResume()
        loadApiData()
    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
           viewModel.getFavHadith(language = getLanguage(), page = pageNo, limit = pageItemCount)
        }
    }

    private fun initObserver()
    {

        viewModel.hadithFavLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()
                CommonResource.EMPTY -> emptyState()
                is HadithResource.hadithFavData -> {

                    morePageBottomLoading(false)

                    totalHadithCount =  it.value.TotalData

                    Log.e("totalHadithCount",totalHadithCount.toString())

                    it.value.Data?.forEach {
                            hadith->
                        val check = hadithData.indexOfFirst { it.Id == hadith.Id}
                        if(check <=0)
                            hadithData.add(hadith)
                    }

                    isNextEnabled = (hadithFavAdapter.itemCount < totalHadithCount) && (totalHadithCount>pageItemCount*pageNo)
                    it.value.Data?.let { it1 -> viewState() }
                }

            }
        }

        viewModel.hadithPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is HadithResource.updateFavFailed ->
                {
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()
                    requireContext().toast("Failed to remove favorite item")
                }
                is HadithResource.setFavHadith ->
                {
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()
                    hadithFavAdapter.delItem(adapterPosition)
                    if (hadithFavAdapter.itemCount == 0)
                        emptyState()
                    requireContext().toast("Favorite list updated successful")
                }

            }
        }
    }

    private fun viewState()
    {
        hadithFavAdapter.update(hadithData)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            viewModel.clear()
        }
    }

    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {

        val btn2 = customAlertDialog?.getBtn2()
        btn2?.isClickable = false
        btn2?.text = btn2?.let { LoadingButton().getInstance(requireContext()).loader(it) }

        lifecycleScope.launch {
            favData?.Id?.let { viewModel.setFavHadith(false,it,getLanguage(),adapterPosition) }
        }

    }

    override fun delFav(data: Data, position: Int) {
        favData = data
        adapterPosition = position
        customAlertDialog?.showDialog(false)
    }

    override fun gotHadithPreview(chapterNo: Int, bookId: Int, bookName: String) {
        val bundle = Bundle().apply {
            putInt("chapterId", chapterNo)
            putInt("bookId", bookId)
            putString("title", bookName)
        }
        gotoFrag(R.id.action_hadithFragment_to_hadithPreviewFragment,data = bundle)
    }

}