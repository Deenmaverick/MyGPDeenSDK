package com.deenislam.sdk.views.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.quran.FavoriteViewModel
import com.deenislam.sdk.views.adapters.quran.FavoriteAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis

class QuranFavoriteFragment : BaseRegularFragment(), otherFagmentActionCallback {

    private val viewmodel by viewModels<FavoriteViewModel>()

    private val favList: RecyclerView by lazy { requireView().findViewById(R.id.favList) }
    private val progressLayout: LinearLayout by lazy { requireView().findViewById(R.id.progressLayout) }
    private val no_internet_layout: NestedScrollView by lazy { requireView().findViewById(R.id.no_internet_layout)}
    private val no_internet_retryBtn: MaterialButton by lazy { requireView().findViewById(R.id.no_internet_retry) }
    private val nodataLayout: NestedScrollView by lazy { requireView().findViewById(R.id.nodataLayout) }


    override fun OnCreate() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainView = layoutInflater.inflate(R.layout.fragment_quran_favorite,container,false)
        setupActionForOtherFragment(0,0,this@QuranFavoriteFragment,"Favorites",true,mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(no_internet_layout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        //init observer
        initObserver()

        //loading start
        loadingState()

        //click retry button for get api data again
        no_internet_retryBtn.setOnClickListener {
            loadApiData()
        }

    }

    override fun onResume() {
        super.onResume()
        // call api to get monthly data
        loadApiData()
    }

    override fun onBackPress() {
        super.onBackPress()
        setupOtherFragment(false)
    }

    private fun viewState()
    {
        val favoriteAdapter = FavoriteAdapter()

        favList.apply {
            adapter = favoriteAdapter
            post {
                dataState()
            }
        }
    }

    private fun initObserver()
    {

    }

    private fun loadApiData()
    {
        viewState()
    }

    private fun loadingState()
    {
        progressLayout.visible(true)
        nodataLayout.visible(false)
        no_internet_layout.visible(false)
    }

    private fun emptyState()
    {
        nodataLayout.visible(true)
        no_internet_layout.visible(false)
        progressLayout.visible(false)
    }

    private fun nointernetState()
    {
        no_internet_layout.visible(true)
        nodataLayout.visible(false)
        progressLayout.visible(false)
    }

    private fun dataState()
    {
        favList.visible(true)
        nodataLayout.visible(false)
        no_internet_layout.visible(false)
        progressLayout.visible(false)
    }


    override fun action1() {

    }

    override fun action2() {

    }

}