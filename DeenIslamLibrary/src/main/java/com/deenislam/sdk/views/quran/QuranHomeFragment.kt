package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.SurahCallback
import com.deenislam.sdk.service.callback.quran.QuranPlayerCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.Data
import com.deenislam.sdk.service.repository.quran.AlQuranRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.viewmodels.quran.AlQuranViewModel
import com.deenislam.sdk.views.adapters.quran.QuranHomePatchAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class QuranHomeFragment : BaseRegularFragment(), SurahCallback, QuranPlayerCallback {

    //private lateinit var recentRC: RecyclerView
    private lateinit var listView:RecyclerView
    private var firstload = false

    private lateinit var quranHomePatchAdapter: QuranHomePatchAdapter
    private lateinit var mainContainer:ConstraintLayout

    private lateinit var viewmodel:AlQuranViewModel


    override fun OnCreate() {
        super.OnCreate()

        val repository = AlQuranRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            quranService = null
        )

        val factory = VMFactory(repository)
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[AlQuranViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_quran_home, container, false)

        //init view
        listView = mainview.findViewById(R.id.listView)
        mainContainer = mainview.findViewById(R.id.mainContainer)
        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(firstload) {
            if(this::quranHomePatchAdapter.isInitialized) {
                listView.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = quranHomePatchAdapter
                }
            }

            baseViewState()
        }
    }


    fun setupActionBar()
    {

        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout

        setupActionForOtherFragment(0,0,null,localContext.resources.getString(R.string.al_quran),true,actionbar)


        listView.post {
            val miniPlayerHeight = getMiniPlayerHeight()
            listView.setPadding(listView.paddingStart,listView.paddingTop,listView.paddingEnd,if(miniPlayerHeight>0) miniPlayerHeight else listView.paddingBottom)
        }

    }


    override fun onResume() {
        super.onResume()
        setupActionBar()

    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible) {
            CallBackProvider.setFragment(this)
            if(!firstload)
                initView()
        }
    }


    private fun initView()
    {
        if(firstload) {

            if(this::quranHomePatchAdapter.isInitialized) {
                listView.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = quranHomePatchAdapter
                }
            }

            baseViewState()
            return
        }


        initObserver()
        loadAPI()

        firstload = true

    }

    override fun noInternetRetryClicked() {
        loadAPI()
    }


    private fun loadAPI()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getQuranHomePatch(getLanguage())
        }
    }



    private fun initObserver()
    {
        viewmodel.homePatchLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranResource.QuranHomePatch -> viewState(it.data)
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()

            }
        }
    }

    private fun viewState(response: DashboardResponse)
    {
        listView.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            quranHomePatchAdapter = QuranHomePatchAdapter(response.Data)
            adapter = quranHomePatchAdapter
            post {
                baseViewState()
            }
        }
    }

    override fun surahClick(surahListData: Data) {

        val bundle = Bundle().apply {
            putInt("surahID", surahListData.SurahId)
            putString("surahName", surahListData.SurahName)
            //putParcelable("suraList", SurahList(chapters = surahList))
        }

        gotoFrag(R.id.action_global_alQuranFragment,data = bundle)
    }

    override fun globalMiniPlayerClosed(){
        listView.setPadding(listView.paddingStart,listView.paddingTop,listView.paddingEnd,16.dp)
    }

    inner class VMFactory(
        private val alQuranRepository: AlQuranRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlQuranViewModel(alQuranRepository) as T
        }
    }

}