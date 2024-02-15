package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.SurahCallback
import com.deenislam.sdk.service.callback.quran.QuranPlayerCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.Data
import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.SurahListResponse
import com.deenislam.sdk.service.repository.quran.AlQuranRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.hideKeyboard
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.viewmodels.quran.AlQuranViewModel
import com.deenislam.sdk.views.adapters.quran.SurahAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.deenislam.sdk.views.main.actionCallback
import com.deenislam.sdk.views.main.searchCallback
import kotlinx.coroutines.launch


internal class QuranSurahFragment : BaseRegularFragment(), SurahCallback, actionCallback,
    searchCallback,
    otherFagmentActionCallback, QuranPlayerCallback {

    private lateinit var viewmodel: AlQuranViewModel

    private lateinit var surahListRC: RecyclerView
    private lateinit var surahAdapter: SurahAdapter
    private lateinit var mainContainer:ConstraintLayout
    private var firstload = false

    private var linearLayoutManager: LinearLayoutManager? = null
    private var surahList: List<Data> = arrayListOf()

    private var pageNo:Int = 1
    private var pageItemCount:Int = 114

    override fun OnCreate() {
        super.OnCreate()

        val repository = AlQuranRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            quranService = null
        )

        viewmodel = AlQuranViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = inflater.inflate(R.layout.fragment_quran_surah, container, false)

        // init view
        surahListRC = mainview.findViewById(R.id.surahListRC)
        mainContainer = mainview.findViewById(R.id.mainContainer)
        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(firstload) {
            initRecyler()
            baseViewState()
        }

    }

    fun setupActionBar()
    {

        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout

        setupActionForOtherFragment(R.drawable.ic_search,0,this@QuranSurahFragment,localContext.resources.getString(R.string.al_quran),true,actionbar)


        surahListRC.post {
            val miniPlayerHeight = getMiniPlayerHeight()
            surahListRC.setPadding(surahListRC.paddingStart,surahListRC.paddingTop,surahListRC.paddingEnd,if(miniPlayerHeight>0)miniPlayerHeight else surahListRC.paddingBottom)
        }
    }

    override fun onResume() {
        super.onResume()
        //CallBackProvider.setFragment(this)
        setupActionBar()
      /*  if (viewmodel.surahListState != null) {
            linearLayoutManager?.onRestoreInstanceState(viewmodel.surahListState)
        }*/


    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible) {
            CallBackProvider.setFragment(this)
            if(!firstload)
                initView()
        }
    }

    override fun onPause() {
        super.onPause()
        surahAdapter.filter.filter("")
        hideSearchbar()
        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout
        actionbar.show()
        view?.let { context?.hideKeyboard(it) }
        viewmodel.surahListState = linearLayoutManager?.onSaveInstanceState()
    }


    private fun initView()
    {

        if(firstload) {
            initRecyler()
            baseViewState()
            return
        }

        initRecyler()
        initObserver()

        loadAPI()

        firstload = true

    }
    private fun initRecyler(){

        if(!this::surahAdapter.isInitialized)
            surahAdapter = SurahAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext())
        surahListRC.apply {
            setPadding(16.dp,15.dp,16.dp,0)
            adapter = surahAdapter
            layoutManager = linearLayoutManager
            overScrollMode = View.OVER_SCROLL_NEVER

        }
    }

    override fun noInternetRetryClicked() {
        loadAPI()
    }

    private fun initObserver()
    {
        viewmodel.surahListLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranResource.SurahList -> viewState(it.data)
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
            }
        }
    }

    private fun loadAPI()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getSurahList(getLanguage(),pageNo,pageItemCount)
        }
    }

    private fun viewState(response: SurahListResponse)
    {

        response.Data?.let {
            surahList = it
            surahAdapter.update(it)
        }
        surahListRC.post {
            baseViewState()
        }
    }


    override fun surahClick(surahListData: Data) {

        val bundle = Bundle().apply {
            putInt("surahID", surahListData.SurahId)
            putString("surahName", surahListData.SurahName)
            //putParcelable("suraList", SurahList(chapters = surahList))
        }

        surahAdapter.filter.filter("")
        hideSearchbar()
        hideKeyboard()
        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout
        actionbar.show()
        gotoFrag(R.id.action_global_alQuranFragment,data = bundle)
    }



    override fun action1() {

        if(surahList.isNotEmpty()) {
            val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout
            actionbar.hide()
            setupSearchbar(this@QuranSurahFragment)
        }
        //dialog_select_surah()
    }

    override fun action2() {
    }

    override fun searchBack() {
        requireContext().hideKeyboard(requireView())
        hideSearchbar()
        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout
        actionbar.show()
        surahAdapter.filter.filter("")
    }

    override fun searchSubmit(query: String) {
        Log.e("searchSubmit",query)
        surahAdapter.filter.filter(query)
    }

    override fun globalMiniPlayerClosed(){
        surahListRC.setPadding(surahListRC.paddingStart,surahListRC.paddingTop,surahListRC.paddingEnd,0)
    }
}