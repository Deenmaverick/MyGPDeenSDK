package com.deenislamic.sdk.views.islamimasaIl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamiMasailCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamiMasailResource
import com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.Data
import com.deenislamic.sdk.service.repository.IslamiMasailRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.LoadingButton
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.IslamiMasailViewModel
import com.deenislamic.sdk.views.adapters.islamimasail.MasailQuestionListAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class MasailBookmarkFragment : BaseRegularFragment(), CustomDialogCallback,
    IslamiMasailCallback {

    private lateinit var listview: RecyclerView

    private lateinit var viewmodel: IslamiMasailViewModel

    private lateinit var actionbar:ConstraintLayout

    private lateinit var masailQuestionListAdapter: MasailQuestionListAdapter
    private var customAlertDialog: CustomAlertDialog? =null

    private var isNextEnabled  = false
    private var pageNo:Int = 1
    private var pageItemCount:Int = 10

    private var favData: Data?=null

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
        actionbar = mainview.findViewById(R.id.actionbar)
        actionbar.hide()
        setupCommonLayout(mainview)

        customAlertDialog = CustomAlertDialog().getInstance()
        customAlertDialog?.setupDialog(
            callback = this,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.cancel),
            btn2Text = localContext.getString(R.string.unfavorite),
            titileText = localContext.getString(R.string.want_to_unfavorite),
            subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
        )

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        masailQuestionListAdapter  = MasailQuestionListAdapter()

        listview.apply {
            setPadding(this.paddingLeft,0,this.paddingRight,this.paddingBottom)
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
            pageNo = 1
            loadapi()

    }

    private fun fetchNextPageData() {
        lifecycleScope.launch {
            pageNo++
            loadapi()
            isNextEnabled = false
        }
    }

    private fun initObserver(){

        viewmodel.islamiMasailFavLivedata.observe(viewLifecycleOwner){

            when(it){

                is CommonResource.EMPTY -> baseEmptyState()
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is IslamiMasailResource.MasailQuestionList ->{
                    isNextEnabled = it.value.Pagination.isNext
                    masailQuestionListAdapter.setLoading(false)
                    it.value.Data?.let { it1 -> masailQuestionListAdapter.update(it1) }
                    baseViewState()
                }

            }
        }

        viewmodel.islamiMasailLivedata.observe(viewLifecycleOwner){

            when(it){
                is IslamiMasailResource.QuestionBookmar ->{
                    masailQuestionListAdapter.delData(it.copy)
                    listview.post {
                        if(masailQuestionListAdapter.itemCount <= 0)
                            baseEmptyState()
                    }
                    customAlertDialog?.dismissDialog()
                    context?.toast(localContext.getString(R.string.favorite_list_updated_successful))
                }
            }
        }
    }

    private fun loadapi(){

        lifecycleScope.launch {
            viewmodel.getFavList(getLanguage(),pageNo,pageItemCount)
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {

        if(favData==null){
            clickBtn1()
            return
        }
        val btn2 = customAlertDialog?.getBtn2()
        btn2?.text = btn2?.let { LoadingButton().getInstance(requireContext()).loader(it) }

        lifecycleScope.launch {
            favData?.let { viewmodel.questionBookmark(it,getLanguage()) }
        }
    }

    override fun questionBookmarkClicked(getdata: Data) {
        favData = getdata
        customAlertDialog?.showDialog(false)
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