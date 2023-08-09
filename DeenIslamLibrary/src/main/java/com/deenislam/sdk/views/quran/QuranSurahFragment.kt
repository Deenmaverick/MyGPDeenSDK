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
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.hideKeyboard
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.quran.SurahViewModel
import com.deenislam.sdk.views.adapters.quran.SurahAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.deenislam.sdk.views.main.searchCallback
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

internal class QuranSurahFragment() : BaseRegularFragment(), SurahCallback, otherFagmentActionCallback,
    searchCallback {

    private lateinit var viewmodel:SurahViewModel

    private lateinit var surahListRC: RecyclerView
    private lateinit var progressLayout:LinearLayout
    private lateinit var no_internet_layout: NestedScrollView
    private lateinit var no_internet_retryBtn: MaterialButton
    private lateinit var surahAdapter:SurahAdapter
    private var firstload:Int = 0

    private var linearLayoutManager: LinearLayoutManager? = null
    private var surahList: List<Chapter> = arrayListOf()

    private lateinit var actionbar:ConstraintLayout

    override fun OnCreate() {
        super.OnCreate()

        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback(this) {
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
        val mainview = inflater.inflate(R.layout.fragment_quran_surah, container, false)

        // init view
        surahListRC = mainview.findViewById(R.id.surahListRC)
        progressLayout = mainview.findViewById(R.id.progressLayout)
        no_internet_layout = mainview.findViewById(R.id.no_internet_layout)
        no_internet_retryBtn = no_internet_layout.findViewById(R.id.no_internet_retry)

        return mainview
    }

    override fun onResume() {
        super.onResume()

        actionbar = (parentFragment as? QuranFragment)?.getActionbar() as ConstraintLayout

        setupActionForOtherFragment(R.drawable.ic_search,0,this@QuranSurahFragment,localContext.resources.getString(R.string.al_quran),true,actionbar)
        if (viewmodel.listState != null) {
            linearLayoutManager?.onRestoreInstanceState(viewmodel.listState)
        }

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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

    override fun onPause() {
        super.onPause()
        surahAdapter.filter.filter("")
        actionbar.show()
        hideSearchbar()
        requireContext().hideKeyboard(requireView())
        viewmodel.listState = linearLayoutManager?.onSaveInstanceState()
    }

    override fun onBackPress() {
        Log.e("onBackPress", "QURAN HOME")
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = Deen.msisdn,
                    pagename = "quran",
                    trackingID = getTrackingID()
                )
            }
            viewmodel.listState = null
        }
        tryCatch { super.onBackPress() }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        //onBackPressedCallback.isEnabled = false
        //onBackPressedCallback.remove()
        Log.e("onBackPress", "QURAN HOME PAUSE")
    }

    private fun initView()
    {

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(no_internet_layout, 10F)

        surahAdapter = SurahAdapter(this@QuranSurahFragment)
        linearLayoutManager = LinearLayoutManager(requireContext())
        surahListRC.apply {
            adapter = surahAdapter
            layoutManager = linearLayoutManager
            overScrollMode = View.OVER_SCROLL_NEVER
            post {
                initObserver()

            }
        }

        no_internet_retryBtn.setOnClickListener {
            loadAPI()
        }

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

    private fun loadAPI()
    {
        lifecycleScope.launch {
            viewmodel.getSurahList_Quran_Com(getLanguage())
        }
    }

    private fun viewState(data: List<Chapter>)
    {
        surahList = data
        surahAdapter.update(data)
        surahListRC.post {
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

        surahAdapter.filter.filter("")
        actionbar.show()
        hideSearchbar()
        hideKeyboard()

        gotoFrag(R.id.action_quranFragment_to_alQuranFragment,data = bundle)
    }



    override fun action1() {

        if(surahList.isNotEmpty()) {
            actionbar.visibility = View.INVISIBLE
            setupSearchbar(this@QuranSurahFragment)
        }
        //dialog_select_surah()
    }

    override fun action2() {
    }


    override fun searchBack() {
        requireContext().hideKeyboard(requireView())
        actionbar.show()
        hideSearchbar()
        surahAdapter.filter.filter("")
    }

    override fun searchSubmit(query: String) {
        Log.e("searchSubmit",query)
        surahAdapter.filter.filter(query)
    }

    inner class VMFactory(
        private val surahRepository: SurahRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SurahViewModel(surahRepository) as T
        }
    }
}