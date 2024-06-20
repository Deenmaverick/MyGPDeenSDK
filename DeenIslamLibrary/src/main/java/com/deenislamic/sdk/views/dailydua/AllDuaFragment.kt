package com.deenislamic.sdk.views.dailydua

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.DailyDuaResource
import com.deenislamic.sdk.service.network.response.dailydua.alldua.Data
import com.deenislamic.sdk.service.repository.DailyDuaRepository
import com.deenislamic.sdk.utils.RECYCLERFOOTER
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.DailyDuaViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.adapters.dailydua.AllDuaAdapter
import com.deenislamic.sdk.views.adapters.dailydua.AllDuaCallback
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

internal class AllDuaFragment : BaseRegularFragment(), AllDuaCallback {

    private lateinit var listView:RecyclerView
    private lateinit var progressLayout:LinearLayout
    private lateinit var nodataLayout:NestedScrollView
    private lateinit var noInternetLayout:NestedScrollView
    private lateinit var noInternetRetry:MaterialButton
    private lateinit var allDuaAdapter: AllDuaAdapter

    private lateinit var viewmodel: DailyDuaViewModel

    private var firstload:Boolean = false

    override fun OnCreate() {
        super.OnCreate()

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

        if(firstload) {
            progressLayout.hide()
        }
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadpage()
    }

    private fun loadpage()
    {

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)


        //click retry button for get api data again
        noInternetRetry.setOnClickListener {
            loadApiData()
        }

        allDuaAdapter = AllDuaAdapter(this@AllDuaFragment)

        val gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (allDuaAdapter.getItemViewType(position)) {
                    RECYCLERFOOTER -> 3
                    else -> 1
                }
            }
        }

        listView.apply {

                adapter = allDuaAdapter
                val margins = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    leftMargin = 8.dp
                    rightMargin = 8.dp
                }
                layoutParams = margins

                layoutManager = gridLayoutManager

        }

        initObserver()

        if(!firstload)
            loadApiData()
        firstload = true

    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "daily_dua",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }


    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getDuaAllCategory(getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.allDayLiveData.observe(viewLifecycleOwner)
        {
            Log.e("allDayLiveData","called")
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()
                CommonResource.EMPTY -> emptyState()
                is DailyDuaResource.duaALlCategory -> viewState(it.data)
            }
        }
    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        noInternetLayout.visible(false)
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
        allDuaAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun selectedCat(id: Int, category: String) {

        val bundle = Bundle().apply {
            putInt("category", id)
            putString("catName", category)
        }
        gotoFrag(R.id.action_global_allDuaPreviewFragment,data = bundle)
    }
}