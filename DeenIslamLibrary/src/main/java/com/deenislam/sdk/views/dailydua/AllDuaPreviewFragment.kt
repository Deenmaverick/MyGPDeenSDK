package com.deenislam.sdk.views.dailydua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
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
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.DailyDuaViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.adapters.dailydua.DuaByCatAdapter
import com.deenislam.sdk.views.adapters.dailydua.DuaByCatCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch

internal class AllDuaPreviewFragment : BaseRegularFragment(), DuaByCatCallback {

    private lateinit var listView: RecyclerView
    private val progressLayout: LinearLayout by lazy { requireView().findViewById(R.id.progressLayout) }
    private val nodataLayout: NestedScrollView by lazy { requireView().findViewById(R.id.nodataLayout) }
    private val noInternetLayout: NestedScrollView by lazy { requireView().findViewById(R.id.no_internet_layout) }
    private val noInternetRetry: MaterialButton by lazy { noInternetLayout.findViewById(R.id.no_internet_retry) }

    private val duaByCatAdapter: DuaByCatAdapter by lazy { DuaByCatAdapter(this@AllDuaPreviewFragment) }

    private lateinit var viewmodel: DailyDuaViewModel
    private val args: AllDuaPreviewFragmentArgs by navArgs()

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

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
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)


        initObserver()

        loadingState()

        //click retry button for get api data again
        noInternetRetry.setOnClickListener {
            loadApiData()
        }

            listView.apply {
                post {
                    adapter = duaByCatAdapter
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                }
            }


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
            viewmodel.getDuaByCategory(args.category,"bn")
        }
    }

    private fun initObserver()
    {
        viewmodel.duaPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()
                CommonResource.EMPTY -> emptyState()
                is DailyDuaResource.duaPreview -> viewState(it.data)
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
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }

    }

    private fun updateFavorite(position: Int, fav: Boolean)
    {
        duaByCatAdapter.update(position,fav)
    }

    override fun favDua(isFavorite: Boolean, duaId: Int, position: Int) {
        lifecycleScope.launch {
            viewmodel.setFavDua(isFavorite,duaId,getLanguage(),position)
        }
    }
}