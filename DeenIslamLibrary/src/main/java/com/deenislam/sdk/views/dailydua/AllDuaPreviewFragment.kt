package com.deenislam.sdk.views.dailydua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.DailyDuaResource
import com.deenislam.sdk.service.network.response.dailydua.duabycategory.Data
import com.deenislam.sdk.service.repository.DailyDuaRepository
import com.deenislam.sdk.utils.Subscription
import com.deenislam.sdk.viewmodels.DailyDuaViewModel
import com.deenislam.sdk.views.adapters.dailydua.DuaByCatAdapter
import com.deenislam.sdk.views.adapters.dailydua.DuaByCatCallback
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch

internal class AllDuaPreviewFragment : BaseRegularFragment(), DuaByCatCallback {

    private lateinit var listView: RecyclerView

    private lateinit var duaByCatAdapter: DuaByCatAdapter

    private lateinit var viewmodel: DailyDuaViewModel
    private val args: AllDuaPreviewFragmentArgs by navArgs()

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).apply {
            duration = 300L
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).apply {
            duration = 300L
        }

        // init viewmodel
        val repository = DailyDuaRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = DailyDuaViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView = localInflater.inflate(R.layout.fragment_all_dua_preview,container,false)
        //init view
        listView = mainView.findViewById(R.id.listView)

        setupActionForOtherFragment(0,0,null,args.catName,true,mainView)
        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPage()

        /*view.postDelayed({
            // Code to execute after the animation
            loadPage()
        }, 300)*/

    }

    private fun loadPage()
    {

        duaByCatAdapter = DuaByCatAdapter(this@AllDuaPreviewFragment)

        initObserver()

        baseLoadingState()


        listView.apply {
            post {
                adapter = duaByCatAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }

    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }

    override fun onResume() {
        super.onResume()
        if(!firstload)
            loadApiData()

        firstload = true
    }


    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getDuaByCategory(args.category,getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.duaPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                CommonResource.EMPTY -> baseEmptyState()
                is DailyDuaResource.duaPreview -> viewState(it.data)
                is DailyDuaResource.setFavDua -> updateFavorite(it.position,it.fav)
            }
        }
    }


    private fun viewState(data: List<Data>)
    {
        duaByCatAdapter.update(data)
        listView.post {
            if(args.duaID>0)
            {
                var adpos = -1
                data.forEach {
                    adpos++
                    if(it.DuaId == args.duaID)
                        listView.scrollToPosition(adpos)
                }
            }

            baseViewState()
        }

    }

    private fun updateFavorite(position: Int, fav: Boolean)
    {
        duaByCatAdapter.update(position,fav)
    }

    override fun favDua(isFavorite: Boolean, duaId: Int, position: Int) {
        if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }
        lifecycleScope.launch {
            viewmodel.setFavDua(isFavorite,duaId,getLanguage(),position)
        }
    }
}