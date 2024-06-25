package com.deenislamic.sdk.views.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.ShareResource
import com.deenislamic.sdk.service.repository.ShareRepository
import com.deenislamic.sdk.viewmodels.ShareViewModel
import com.deenislamic.sdk.views.adapters.share.BackgroundAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch

internal class BackgroundFragment(private val serial: Int) : BaseRegularFragment() {

    private lateinit var wallpaperList:RecyclerView
    private lateinit var backgroundAdapter: BackgroundAdapter

    private lateinit var viewmodel: ShareViewModel

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()

        val repository = ShareRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = ShareViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_background, container, false)

        wallpaperList = mainview.findViewById(R.id.wallpaperList)

        setupCommonLayout(mainview)
        return mainview
    }


    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible){

            if(firstload)
                return
            else {
                initObserver()
                loadapi()
            }
        }
    }


    private fun initObserver(){

        viewmodel.shareLiveData.observe(viewLifecycleOwner){

            when(it){

                is ShareResource.Wallpaper ->{

                    wallpaperList.apply {
                        backgroundAdapter = BackgroundAdapter(it.data)
                        adapter = backgroundAdapter
                        layoutManager = GridLayoutManager(requireContext(),2)
                    }

                    baseViewState()
                    firstload = true
                }

                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
            }
        }

    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    private fun loadapi(){
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getWallpaper(getLanguage(),serial)
        }
    }

}