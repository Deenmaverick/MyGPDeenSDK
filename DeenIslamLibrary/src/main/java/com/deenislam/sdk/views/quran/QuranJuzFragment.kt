package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.models.quran.SurahResource
import com.deenislam.sdk.service.network.response.quran.juz.Juz
import com.deenislam.sdk.service.network.response.quran.juz.JuzResponse
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.SurahList
import com.deenislam.sdk.service.repository.quran.AlQuranRepository
import com.deenislam.sdk.service.repository.quran.SurahRepository
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.quran.AlQuranViewModel
import com.deenislam.sdk.viewmodels.quran.SurahViewModel
import com.deenislam.sdk.views.adapters.quran.JuzCallback
import com.deenislam.sdk.views.adapters.quran.QuranJuzAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

internal class QuranJuzFragment() : BaseRegularFragment(), JuzCallback {

    private lateinit var  juzRC: RecyclerView
    private lateinit var progressLayout:LinearLayout
    private lateinit var no_internet_layout: NestedScrollView
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var no_internet_retryBtn: MaterialButton

    private var juzList: List<Juz> = arrayListOf()
    private lateinit var quranJuzAdapter:QuranJuzAdapter

    private var firstload:Int = 0

    private lateinit var  viewmodel: AlQuranViewModel
    private lateinit var surahViewmodel: SurahViewModel

    private var surahList: ArrayList<Chapter> = arrayListOf()

    override fun OnCreate() {
        super.OnCreate()

        val repository = AlQuranRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            quranService = NetworkProvider().getInstance().provideQuranService()
        )
        viewmodel = AlQuranViewModel(repository)

        val surahRepository = SurahRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            quranService = NetworkProvider().getInstance().provideQuranService()
        )

        val factory = VMFactory(surahRepository)
        surahViewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[SurahViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_quran_surah, container, false)

        //init view
        juzRC = mainview.findViewById(R.id.surahListRC)
        progressLayout = mainview.findViewById(R.id.progressLayout)
        no_internet_layout = mainview.findViewById(R.id.no_internet_layout)
        nodataLayout = mainview.findViewById(R.id.nodataLayout)
        no_internet_retryBtn = no_internet_layout.findViewById(R.id.no_internet_retry)


        return mainview
    }

    override fun onResume() {
        super.onResume()
        val actionbar  =  (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout
        setupActionForOtherFragment(0,0,null,localContext.resources.getString(R.string.al_quran),true,actionbar)

    }

    override fun onBackPress() {

        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "quran",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible)
        {
            if(firstload == 0)
                loadAPI()
            firstload = 1
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(no_internet_layout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        initView()

    }


    private fun initView()
    {

        quranJuzAdapter = QuranJuzAdapter(this@QuranJuzFragment)
        juzRC.apply {
            adapter = quranJuzAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            post {
                initObserver()
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
            viewmodel.juzList()
        }
    }
    private fun initObserver()
    {
        viewmodel.juzLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranResource.juzList -> viewState(it.juzs)
                is CommonResource.API_CALL_FAILED -> noInternetState()
                is CommonResource.EMPTY -> emptyState()
            }
        }

        surahViewmodel.surahlist.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is SurahResource.getSurahList_quran_com ->
                {
                    surahList.clear()
                    surahList.addAll(it.data)
                    quranJuzAdapter.updateSurahList(it.data)
                }

            }
        }
    }



    private fun noInternetState()
    {
        nodataLayout.hide()
        progressLayout.hide()
        no_internet_layout.show()
    }

    private fun emptyState()
    {
        progressLayout.hide()
        nodataLayout.show()
        no_internet_layout.hide()
    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        no_internet_layout.visible(false)
    }

    private fun viewState(data: List<Juz>)
    {
        juzList = data
        quranJuzAdapter.update(data)
        juzRC.post {
            if (juzList.isNotEmpty())
            {

                val verse_mapping = juzList[0].verse_mapping::class.java.declaredFields
                var suraSubTxt = ""

                if(surahList.size == 114) {
                    for (surah in verse_mapping) {
                        surah.isAccessible = true

                        val value = surah.get(juzList[0].verse_mapping)

                        if (value is String && value.isNotEmpty()) {
                            suraSubTxt += "${surahList[surah.name.toInt()-1].name_simple} "
                        }
                    }
                }

                if(suraSubTxt.length>30)
                    suraSubTxt = "${suraSubTxt.substring(0,30)}..."

            }
            progressLayout.visible(false)
            nodataLayout.hide()
            no_internet_layout.visible(false)
        }
    }

    override fun juzClicked(juz: Juz) {

        val bundle = Bundle().apply {
            putParcelable("juz", juz)
            putParcelable("juzList", JuzResponse(juzs = juzList))
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