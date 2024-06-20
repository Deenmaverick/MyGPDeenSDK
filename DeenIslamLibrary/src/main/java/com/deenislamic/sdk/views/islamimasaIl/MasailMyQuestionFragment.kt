package com.deenislamic.sdk.views.islamimasaIl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamiMasailCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamiMasailResource
import com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.Data
import com.deenislamic.sdk.service.repository.IslamiMasailRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.viewmodels.IslamiMasailViewModel
import com.deenislamic.sdk.views.adapters.islamimasail.MasailQuestionListAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class MasailMyQuestionFragment : BaseRegularFragment(), IslamiMasailCallback {

    private lateinit var listview: RecyclerView

    private lateinit var viewmodel: IslamiMasailViewModel

    private lateinit var masailQuestionListAdapter: MasailQuestionListAdapter

    private var isNextEnabled  = false
    private var pageNo:Int = 1
    private var pageItemCount:Int = 10
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        // init viewmodel
        val repository = IslamiMasailRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = IslamiMasailViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_masail_question_cat,container,false)

        listview = mainview.findViewById(R.id.listview)

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        masailQuestionListAdapter  = MasailQuestionListAdapter()

        listview.apply {
            setPadding(16.dp,0,16.dp,this.paddingBottom)
            adapter = masailQuestionListAdapter
        }

        listview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    // RecyclerView has reached the end.
                    // Load more data here if you are implementing pagination.
                    // NestedScrollView has scrolled to the end
                    if(isNextEnabled && masailQuestionListAdapter.itemCount>0) {
                        fetchNextPageData()
                        masailQuestionListAdapter.setLoading(true)
                    }
                    else
                        masailQuestionListAdapter.setLoading(false)
                }
            }
        })

        initObserver()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible){
            CallBackProvider.setFragment(this)
            loadpage()
        }
    }


    private fun loadpage(){

        CallBackProvider.setFragment(this)
        if(firstload)
            return
        loadapi()
        firstload = true

    }

    private fun fetchNextPageData() {
        lifecycleScope.launch {
            pageNo++
            loadapi()
            isNextEnabled = false
        }
    }

    private fun initObserver(){

        viewmodel.islamiMasailLivedata.observe(viewLifecycleOwner){
            when(it){
                is CommonResource.EMPTY -> baseEmptyState()
                is CommonResource.API_CALL_FAILED -> baseEmptyState()
                is IslamiMasailResource.MasailQuestionList ->{
                    isNextEnabled = it.value.Pagination.isNext
                    masailQuestionListAdapter.setLoading(false)
                    it.value.Data?.let { it1 -> masailQuestionListAdapter.update(it1) }
                    baseViewState()
                }
                is IslamiMasailResource.QuestionBookmar ->{
                    masailQuestionListAdapter.updateFav(it.copy)
                }
            }
        }
    }

    private fun loadapi(){

        lifecycleScope.launch {
            viewmodel.getMyQuestions(getLanguage(),pageNo,pageItemCount)
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    override fun questionBookmarkClicked(getdata: Data) {
        lifecycleScope.launch {
            viewmodel.questionBookmark(getdata,getLanguage())
        }
    }

    override fun masailQuestionClicked(getdata: Data) {
        val bundle = Bundle()
        bundle.putInt("qid",getdata.Id)
        gotoFrag(R.id.action_global_masailAnswerFragment,bundle)
    }

    override fun questionShareClicked(getdata: Data) {
        val bundle = Bundle()
        bundle.putInt("qid",getdata.Id)
        bundle.putBoolean("isShare",true)
        gotoFrag(R.id.action_global_masailAnswerFragment,bundle)
    }
}