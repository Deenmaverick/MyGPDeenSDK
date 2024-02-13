package com.deenislam.sdk.views.islamimasaIl

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
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
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.adapters.islamimasail.MasailQuestionCatAdapter
import kotlinx.coroutines.launch


internal class MasailQuestionCatFragment : BaseRegularFragment(), IslamiMasailCallback {

    private lateinit var listview:RecyclerView
    private lateinit var masailQuestionCatAdapter: MasailQuestionCatAdapter

    private lateinit var viewmodel: IslamiMasailViewModel

    private var firstload = false

    private var isNextEnabled  = true
    private var pageNo:Int = 1
    private var pageItemCount:Int = 10


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

        masailQuestionCatAdapter  = MasailQuestionCatAdapter()

        listview.apply {
            adapter = masailQuestionCatAdapter
        }

        listview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    // RecyclerView has reached the end.
                    // Load more data here if you are implementing pagination.
                    Log.e("RecyclerView_Listview", "Reached the end")
                    // NestedScrollView has scrolled to the end
                    if(isNextEnabled && masailQuestionCatAdapter.itemCount>0) {
                        fetchNextPageData()
                        masailQuestionCatAdapter.setLoading(true)
                    }
                    else
                        masailQuestionCatAdapter.setLoading(false)
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
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is IslamiMasailResource.MasailCatList ->{
                    isNextEnabled = it.data.Pagination.isNext
                    masailQuestionCatAdapter.setLoading(false)
                    masailQuestionCatAdapter.update(it.data.Data)
                    baseViewState()
                }
            }
        }
    }

    private fun loadapi(){

        lifecycleScope.launch {
            viewmodel.getMasailCat(getLanguage(),pageNo,pageItemCount)
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    override fun masailCatClicked(getdata: Data) {
        val bundle = Bundle()
        bundle.putInt("catid",getdata.Id)
        bundle.putString("title",getdata.category)
        gotoFrag(R.id.action_global_masailQuestionByCatFragment,bundle)
    }
}