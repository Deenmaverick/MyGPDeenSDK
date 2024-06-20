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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.HadithResource
import com.deenislamic.sdk.service.network.response.hadith.preview.Data
import com.deenislamic.sdk.service.repository.HadithRepository
import com.deenislamic.sdk.utils.LoadingButton
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.HadithViewModel
import com.deenislamic.sdk.views.adapters.hadith.HadithFavAdapter
import com.deenislamic.sdk.views.adapters.hadith.hadithFavCallback
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch

internal class HadithFavoriteFragment(private val checkFirstload: Boolean = false) : BaseRegularFragment(), CustomDialogCallback,
    hadithFavCallback {

    private lateinit var listView: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton
    private lateinit var last_item_loading_progress: CircularProgressIndicator

    private lateinit var hadithFavAdapter: HadithFavAdapter

    private var customAlertDialog: CustomAlertDialog? =null

    private var isNextEnabled  = true
    private var pageNo:Int = 1
    private var pageItemCount:Int = 10
    private var totalHadithCount = 0

    private var favData: Data? =null
    private var adapterPosition:Int = -1
    private lateinit var viewModel:HadithViewModel
    private var firstload = false


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
       /* customAlertDialog?.setupDialog(
            callback = this@HadithFavoriteFragment,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.cancel),
            btn2Text = localContext.getString(R.string.delete),
            titileText = localContext.getString(R.string.want_to_delete),
            subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
        )*/


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
                        fetchNextPageData()
                        Log.e("ALQURAN_SCROLL","END")
                        morePageBottomLoading(true)
                    }
                    else
                        morePageBottomLoading(false)
                }


            }
        })

        initObserver()

    }

    private fun fetchNextPageData() {

        lifecycleScope.launch {
                pageNo++
                loadApiData()
                isNextEnabled = false
            }

    }

    private fun morePageBottomLoading(bol:Boolean)
    {
        last_item_loading_progress.visible(bol)
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

                    isNextEnabled = it.value.Pagination.isNext
                    totalHadithCount =  it.value.TotalData

                    Log.e("totalHadithCount",totalHadithCount.toString())
                   // isNextEnabled = (hadithFavAdapter.itemCount < totalHadithCount) && (totalHadithCount>pageItemCount*pageNo)
                    it.value.Data?.let { it1 -> viewState(it1) }
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
                    requireContext().toast(localContext.getString(R.string.failed_to_remove_favorite_item))
                }
                is HadithResource.setFavHadith ->
                {
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()
                    hadithFavAdapter.delItem(adapterPosition)
                    if (hadithFavAdapter.itemCount == 0) {

                        lifecycleScope.launch {
                            viewModel.clearFavLiveData()
                        }
                        emptyState()
                    }
                    requireContext().toast(localContext.getString(R.string.favorite_list_updated_successful))
                }

            }
        }
    }

    private fun viewState(data: List<Data>)
    {
        hadithFavAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
            lifecycleScope.launch {
                viewModel.clearFavLiveData()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isNextEnabled  = true
        pageNo = 1
        favData =null
        adapterPosition = -1
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
            favData?.Id?.let { viewModel.setFavHadith(true,it,getLanguage(),adapterPosition) }
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
        gotoFrag(R.id.action_global_hadithPreviewFragment,data = bundle)
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            viewModel.clear()
        }
    }
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible){
            if(checkFirstload && !firstload) {
                baseLoadingState()
                loadApiData()
            }else if(!checkFirstload)
                loadApiData()

            firstload = true

            customAlertDialog?.setupDialog(
                callback = this@HadithFavoriteFragment,
                context = requireContext(),
                btn1Text = localContext.getString(R.string.cancel),
                btn2Text = localContext.getString(R.string.delete),
                titileText = localContext.getString(R.string.want_to_delete),
                subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
            )

        }
    }
}