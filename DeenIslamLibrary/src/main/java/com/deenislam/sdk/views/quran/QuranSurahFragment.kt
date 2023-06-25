package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.SurahResource
import com.deenislam.sdk.service.network.response.quran.SurahListData
import com.deenislam.sdk.service.repository.quran.SurahRepository
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.quran.SurahViewModel
import com.deenislam.sdk.views.adapters.quran.SurahAdapter
import com.deenislam.sdk.views.adapters.quran.SurahCallback
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class QuranSurahFragment : BaseRegularFragment(),SurahCallback {

    private lateinit var viewmodel:SurahViewModel

    private val surahListRC: RecyclerView by lazy { requireView().findViewById(R.id.surahListRC) }
    private val progressLayout:LinearLayout by lazy { requireView().findViewById(R.id.progressLayout) }
    private val no_internet_layout: NestedScrollView by lazy { requireView().findViewById(R.id.no_internet_layout)}
    private val no_internet_retryBtn: MaterialButton by lazy { requireView().findViewById(R.id.no_internet_retry) }
    private val surahAdapter:SurahAdapter by lazy { SurahAdapter(this@QuranSurahFragment) }
    private var firstload:Int = 0

    override fun OnCreate() {
        super.OnCreate()

        val repository = SurahRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = SurahViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quran_surah, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView()
    {
        if(firstload != 0)
            return
        firstload = 1


        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(no_internet_layout, 10F)

        surahListRC.apply {
            adapter = surahAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            post {
                initObserver()
                loadAPI()
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
                is SurahResource.getSurahList -> viewState(it.data)
                is CommonResource.API_CALL_FAILED -> noInternetState()
            }
        }
    }

    private fun loadAPI()
    {
        lifecycleScope.launch {
            viewmodel.getSurahList("en")
        }
    }

    private fun viewState(data: List<SurahListData>)
    {
        surahAdapter.update(data)
        surahListRC.post {
            progressLayout.visible(false)
            no_internet_layout.visible(false)
        }

    }

    private fun noInternetState()
    {
        no_internet_layout.show()
    }

    override fun surahClick(surahListData: SurahListData) {

        val bundle = Bundle().apply {
            putParcelable("surah", surahListData)
        }

        gotoFrag(R.id.alQuranFragment,data = bundle)
    }
}