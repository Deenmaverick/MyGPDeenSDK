package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.SurahCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.SurahResource
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.SurahList
import com.deenislam.sdk.service.repository.quran.SurahRepository
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.runWhenReady
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.quran.SurahViewModel
import com.deenislam.sdk.views.adapters.quran.PopularSurahAdapter
import com.deenislam.sdk.views.adapters.quran.RecentlyReadAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.coroutines.launch

internal class QuranHomeFragment() : BaseRegularFragment(), SurahCallback {

    //private lateinit var recentRC: RecyclerView
    private lateinit var popularRC: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private var firstload:Int = 0
    private var surahList: ArrayList<Chapter> = arrayListOf()

    private lateinit var no_internet_layout: NestedScrollView
    private lateinit var no_internet_retryBtn: MaterialButton

    private lateinit var popularSurahAdapter:PopularSurahAdapter

    private lateinit var viewmodel: SurahViewModel

    override fun OnCreate() {
        super.OnCreate()

        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback {
                onBackPress()
            }
        onBackPressedCallback.isEnabled = true


        // init viewmodel
        val repository = SurahRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            quranService = NetworkProvider().getInstance().provideQuranService()
        )

        val factory = VMFactory(repository)
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[SurahViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_quran_home, container, false)

        //init view
        //recentRC = mainview.findViewById(R.id.recentRC)
        popularRC = mainview.findViewById(R.id.popularRC)
        progressLayout = mainview.findViewById(R.id.progressLayout)
        no_internet_layout = mainview.findViewById(R.id.no_internet_layout)
        no_internet_retryBtn = mainview.findViewById(R.id.no_internet_retry)

         return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()

        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout

        setupActionForOtherFragment(0,0,null,localContext.resources.getString(R.string.al_quran),true,actionbar)

    }



    private fun initView()
    {
            ViewCompat.setTranslationZ(progressLayout, 10F)

           /* recentRC.apply {
                adapter = RecentlyReadAdapter()
                ViewPagerHorizontalRecyler().getInstance().load(this)
                overScrollMode = View.OVER_SCROLL_NEVER

            }*/

            popularSurahAdapter = PopularSurahAdapter(this@QuranHomeFragment)

            popularRC.apply {
                adapter = popularSurahAdapter
                overScrollMode = View.OVER_SCROLL_NEVER

                runWhenReady {
                    initObserver()

                    if(firstload == 0)
                        loadAPI()
                    firstload = 1

                }
            }

            no_internet_retryBtn.setOnClickListener {
                loadAPI()
            }

    }

    private fun loadAPI()
    {
        loadingState()
        lifecycleScope.launch {
            viewmodel.getSurahList_Quran_Com(getLanguage())
        }
    }

    override fun onBackPress() {

        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = Deen.msisdn,
                    pagename = "quran",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        no_internet_layout.visible(false)
    }

    private fun initObserver()
    {
        viewmodel.surahlist.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is SurahResource.getSurahList_quran_com -> viewState(it.data)
                is CommonResource.API_CALL_FAILED -> noInternetState()
            }
        }
    }

    private fun viewState(data: List<Chapter>)
    {
        surahList.clear()
        surahList.addAll(data)

        Log.e("viewState",Gson().toJson(data))

        val filterSurahList: ArrayList<Chapter> = arrayListOf()
        data.forEach {
            when(it.id)
            {
                1 -> filterSurahList.add(it)
                2 -> filterSurahList.add(it)
                18 -> filterSurahList.add(it)
                36 -> filterSurahList.add(it)
                67 -> filterSurahList.add(it)
                112 -> filterSurahList.add(it)
                108 -> filterSurahList.add(it)
                109 -> filterSurahList.add(it)
                113 -> filterSurahList.add(it)
                114 -> filterSurahList.add(it)
            }
        }

        popularSurahAdapter.update(filterSurahList)
        popularRC.post {
            progressLayout.visible(false)
            no_internet_layout.visible(false)
        }

    }

    private fun noInternetState()
    {
        progressLayout.hide()
        no_internet_layout.show()
    }

    override fun surahClick(surahListData: Chapter) {

        val bundle = Bundle().apply {
            putParcelable("surah", surahListData)
            putParcelable("suraList", SurahList(chapters = surahList))
        }

        gotoFrag(R.id.action_quranFragment_to_alQuranFragment,data = bundle)
    }

    inner class VMFactory(
        private val surahRepository: SurahRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SurahViewModel(surahRepository) as T
        }
    }

}