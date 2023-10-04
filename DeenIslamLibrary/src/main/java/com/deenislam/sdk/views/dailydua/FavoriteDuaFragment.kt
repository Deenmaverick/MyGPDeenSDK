package com.deenislam.sdk.views.dailydua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislam.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.DailyDuaResource
import com.deenislam.sdk.service.network.response.dailydua.favdua.Data
import com.deenislam.sdk.service.repository.DailyDuaRepository
import com.deenislam.sdk.utils.LoadingButton
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.DailyDuaViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.adapters.dailydua.FavDuaAdapterCallback
import com.deenislam.sdk.views.adapters.dailydua.FavoriteDuaAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

internal class FavoriteDuaFragment : BaseRegularFragment(), FavDuaAdapterCallback,
    CustomDialogCallback {

    private lateinit var listView:RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton

    private var customAlertDialog: CustomAlertDialog? =null

    private var duaID:Int = 0
    private var adapterPosition:Int = 0

    private lateinit var favoriteDuaAdapter: FavoriteDuaAdapter

    private lateinit var viewmodel:DailyDuaViewModel

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

        customAlertDialog = CustomAlertDialog().getInstance()
        customAlertDialog?.setupDialog(
            callback = this@FavoriteDuaFragment,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.cancel),
            btn2Text = localContext.getString(R.string.delete),
            titileText = localContext.getString(R.string.want_to_delete),
            subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
        )

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPage()
    }


   /* override fun onResume() {
        super.onResume()

    }*/

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
    private fun loadPage()
    {

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        initObserver()


        favoriteDuaAdapter = FavoriteDuaAdapter(this@FavoriteDuaFragment)

        listView.apply {

                val margins = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    leftMargin = 16.dp
                    rightMargin = 16.dp
                }
                layoutParams = margins

                adapter = favoriteDuaAdapter

                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            post {
                loadApiData()
            }
        }

    }



    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getFavDua(getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.favDuaLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()

                CommonResource.EMPTY -> emptyState()
                is DailyDuaResource.favDuaList -> viewState(it.data)

            }
        }

        viewmodel.duaPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.ACTION_API_CALL_FAILED ->
                {
                    customAlertDialog?.dismissDialog()
                    requireContext().toast(localContext.getString(R.string.something_went_wrong_try_again))
                }

                is DailyDuaResource.setFavDua ->
                {
                    favoriteDuaAdapter.delItem(it.position)

                    listView.post {
                        if(favoriteDuaAdapter.itemCount <= 0)
                            emptyState()
                    }
                    customAlertDialog?.dismissDialog()

                }
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
        favoriteDuaAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            viewmodel.clearFavDuaLiveData()
        }
    }

    override fun favClick(duaID: Int, position: Int) {
        this.duaID = duaID
        adapterPosition = position
        customAlertDialog?.showDialog(false)
    }

    override fun duaClick(duaId: Int, subCategory: Int, category: String) {

        val bundle = Bundle().apply {
            putInt("category", subCategory)
            putString("catName", category)
            putInt("duaID", duaId)
        }
        gotoFrag(R.id.action_dailyDuaFragment_to_allDuaPreviewFragment,data = bundle)
    }

    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {
        val btn2 = customAlertDialog?.getBtn2()
        btn2?.text = btn2?.let { LoadingButton().getInstance(requireContext()).loader(it) }

        lifecycleScope.launch {
            viewmodel.setFavDua(isFavorite = true, duaId = duaID, language = getLanguage(),adapterPosition)
        }
    }
}
