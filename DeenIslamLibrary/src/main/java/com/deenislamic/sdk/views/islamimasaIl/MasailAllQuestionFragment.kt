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
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.adapters.islamimasail.MasailHomeAdapter
import kotlinx.coroutines.launch


internal class MasailAllQuestionFragment : BaseRegularFragment(), IslamiMasailCallback {

    private lateinit var listview:RecyclerView
    private lateinit var masailHomeAdapter: MasailHomeAdapter
    private lateinit var viewmodel: IslamiMasailViewModel
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
        CallBackProvider.setFragment(this)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        masailHomeAdapter = MasailHomeAdapter()

        listview.apply {
            setPadding(16.dp,0,16.dp,this.paddingBottom)
            adapter = masailHomeAdapter
        }

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
    }

    private fun loadapi(){
        lifecycleScope.launch {
            viewmodel.getHomePatch(getLanguage())
        }
    }

    private fun initObserver(){

        viewmodel.islamiMasailLivedata.observe(viewLifecycleOwner){

            when(it){
                is CommonResource.EMPTY -> baseEmptyState()
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is IslamiMasailResource.HomePatch ->{
                    masailHomeAdapter.update(it.data)
                    baseViewState()
                }
            }
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
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