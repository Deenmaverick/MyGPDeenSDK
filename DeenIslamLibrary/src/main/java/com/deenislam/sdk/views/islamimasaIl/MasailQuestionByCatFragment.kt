package com.deenislam.sdk.views.islamimasaIl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.IslamiMasailCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamiMasailResource
import com.deenislam.sdk.service.network.response.islamimasail.catlist.Data
import com.deenislam.sdk.service.repository.IslamiMasailRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.IslamiMasailViewModel
import com.deenislam.sdk.views.adapters.islamimasail.MasailQuestionListAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class MasailQuestionByCatFragment : BaseRegularFragment(), IslamiMasailCallback {

    private lateinit var viewmodel: IslamiMasailViewModel

    private lateinit var masailQuestionListAdapter: MasailQuestionListAdapter

    private val navArgs:MasailQuestionByCatFragmentArgs by navArgs()

    private lateinit var listview:RecyclerView

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
        val mainview = localInflater.inflate(R.layout.fragment_masail_question_by_cat,container,false)

        listview = mainview.findViewById(R.id.listview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = navArgs.title,
            backEnable = true,
            view = mainview
        )

        CallBackProvider.setFragment(this)

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*  if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()
    }

    private fun loadpage(){

        if(!this::masailQuestionListAdapter.isInitialized)
        masailQuestionListAdapter = MasailQuestionListAdapter()

        listview.apply {
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

        if(firstload) {
            baseViewState()
            return
        }


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

    private fun loadapi(){

        lifecycleScope.launch {
            viewmodel.getMasailQuestionByCat(getLanguage(),pageNo,pageItemCount,navArgs.catid)
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun initObserver(){

        viewmodel.islamiMasailLivedata.observe(viewLifecycleOwner){

            when(it){
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
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




    override fun questionBookmarkClicked(getdata: com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data) {

        lifecycleScope.launch {
            viewmodel.questionBookmark(getdata,getLanguage())
        }
    }

    override fun masailQuestionClicked(getdata: com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data) {
        val bundle = Bundle()
        bundle.putInt("qid",getdata.Id)
        gotoFrag(R.id.action_global_masailAnswerFragment,bundle)
    }

    override fun questionShareClicked(getdata: com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data) {
        val bundle = Bundle()
        bundle.putInt("qid",getdata.Id)
        bundle.putBoolean("isShare",true)
        gotoFrag(R.id.action_global_masailAnswerFragment,bundle)
    }
}