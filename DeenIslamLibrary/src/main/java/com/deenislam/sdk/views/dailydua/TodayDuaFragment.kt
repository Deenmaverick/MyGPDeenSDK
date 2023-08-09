package com.deenislam.sdk.views.dailydua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.isEmpty
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.DailyDuaResource
import com.deenislam.sdk.service.network.response.dailydua.todaydua.Data
import com.deenislam.sdk.service.repository.DailyDuaRepository
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.DailyDuaViewModel
import com.deenislam.sdk.views.adapters.dailydua.TodayDuaAdapter
import com.deenislam.sdk.views.adapters.dailydua.TodayDuaCallback
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch


internal class TodayDuaFragment : BaseRegularFragment(), TodayDuaCallback {

    private lateinit var listView:RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton

    private var firstload:Boolean = false

    private lateinit var viewmodel: DailyDuaViewModel

    private lateinit var todayDuaAdapter: TodayDuaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback {
                onBackPress()
            }
        onBackPressedCallback.isEnabled = true


        // init viewmodel
        val repository = DailyDuaRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = DailyDuaViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_daily_dua_page,container,false)

        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadpage()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible)
        {
            if(!firstload)
                loadApiData()
            firstload = true

        }
    }

    private fun loadpage()
    {
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        initObserver()

        todayDuaAdapter = TodayDuaAdapter(this@TodayDuaFragment)

        listView.apply {
                adapter = todayDuaAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        //click retry button for get api data again
        noInternetRetry.setOnClickListener {
            loadApiData()
        }

    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getTodayDua(getLanguage())
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = Deen.msisdn,
                    pagename = "daily_dua",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }
    private fun initObserver()
    {
        viewmodel.todayDuaLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> noInternetState()
                is CommonResource.EMPTY -> emptyState()
                is DailyDuaResource.todayDuaList -> viewState(it.data)
            }
        }

        viewmodel.duaPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is DailyDuaResource.setFavDua -> updateFavorite(it.position,it.fav)
            }
        }
    }
    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        noInternetLayout.visible(false)
    }

    private fun updateFavorite(position: Int, fav: Boolean)
    {
        todayDuaAdapter.update(position,fav)
    }

    private fun emptyState()
    {
        progressLayout.hide()
        nodataLayout.show()
        noInternetLayout.hide()
    }

    private fun noInternetState()
    {
        progressLayout.hide()
        nodataLayout.hide()
        noInternetLayout.show()
    }

    private fun viewState(data: List<Data>)
    {
        todayDuaAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun favClick(isFavorite: Boolean, duaId: Int, position: Int) {
        lifecycleScope.launch {
            viewmodel.setFavDua(isFavorite,duaId,"en",position)
        }
    }
}