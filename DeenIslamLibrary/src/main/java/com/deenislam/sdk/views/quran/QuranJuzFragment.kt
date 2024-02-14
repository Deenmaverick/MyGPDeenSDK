package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.quran.QuranPlayerCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.network.response.quran.qurangm.paralist.Data
import com.deenislam.sdk.service.network.response.quran.qurangm.paralist.ParaListResponse
import com.deenislam.sdk.service.repository.quran.AlQuranRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.quran.AlQuranViewModel
import com.deenislam.sdk.views.adapters.quran.JuzCallback
import com.deenislam.sdk.views.adapters.quran.QuranJuzAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch

internal class QuranJuzFragment : BaseRegularFragment(), JuzCallback, QuranPlayerCallback {

    private lateinit var viewmodel: AlQuranViewModel

    private lateinit var  juzRC: RecyclerView

    private var juzList: List<Data> = arrayListOf()
    private lateinit var quranJuzAdapter: QuranJuzAdapter
    private lateinit var mainContainer:ConstraintLayout

    private var firstload:Int = 0

    private var pageNo:Int = 1
    private var pageItemCount:Int = 30

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
        val mainview = localInflater.inflate(R.layout.fragment_quran_surah, container, false)

        //init view
        juzRC = mainview.findViewById(R.id.surahListRC)
        mainContainer = mainview.findViewById(R.id.mainContainer)
        setupCommonLayout(mainview)

        return mainview
    }



    fun setupActionBar()
    {
        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout
        setupActionForOtherFragment(0,0,null,localContext.resources.getString(R.string.al_quran),true,actionbar)

        juzRC.post {
            val miniPlayerHeight = getMiniPlayerHeight()
            juzRC.setPadding(0,juzRC.paddingTop,0,miniPlayerHeight)
        }
    }



    override fun onResume() {
        super.onResume()
        //CallBackProvider.setFragment(this)
        setupActionBar()
        initView()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible) {
            CallBackProvider.setFragment(this)
        }
    }

    private fun initView()
    {
        if(firstload != 0)
            return
        firstload = 1


        quranJuzAdapter = QuranJuzAdapter()
        juzRC.apply {
            adapter = quranJuzAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        initObserver()
        loadAPI()

    }

    override fun noInternetRetryClicked() {
        loadAPI()
    }

    private fun loadAPI()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getParaList(getLanguage(),pageNo,pageItemCount)
        }
    }
    private fun initObserver()
    {
        viewmodel.paraListLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranResource.ParaList -> viewState(it.data)
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
            }
        }

    }


    private fun viewState(response: ParaListResponse)
    {
        juzList = response.Data
        quranJuzAdapter.update(response.Data)
        juzRC.post {
            baseViewState()
        }
    }

    override fun juzClicked(juz: Data) {

        val bundle = Bundle().apply {
            putParcelable("juz", juz)
            putParcelableArray("juzList", juzList.toTypedArray())
            //putParcelable("suraList", SurahList(chapters = surahList))
        }
        gotoFrag(R.id.action_global_alQuranFragment,data = bundle)
    }

    override fun globalMiniPlayerClosed(){
        juzRC.setPadding(0,juzRC.paddingTop,0,0)
    }
}