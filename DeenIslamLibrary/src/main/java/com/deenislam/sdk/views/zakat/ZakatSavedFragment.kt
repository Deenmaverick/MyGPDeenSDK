package com.deenislam.sdk.views.zakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislam.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.ZakatResource
import com.deenislam.sdk.service.network.response.zakat.Data
import com.deenislam.sdk.service.repository.ZakatRepository
import com.deenislam.sdk.utils.LoadingButton
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.ZakatViewModel
import com.deenislam.sdk.views.adapters.ZakatAdapterCallback
import com.deenislam.sdk.views.adapters.ZakatSavedAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

internal class ZakatSavedFragment : BaseRegularFragment(), CustomDialogCallback,
    ZakatAdapterCallback {

    private lateinit var listView: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton

    private var customAlertDialog: CustomAlertDialog? =null

    private var zakatHistoryData: Data? =null
    private var adapterPosition:Int = -1
    private val zakatSavedAdapter: ZakatSavedAdapter by lazy { ZakatSavedAdapter(this@ZakatSavedFragment) }

    private lateinit var viewmodel: ZakatViewModel

    override fun OnCreate() {
        super.OnCreate()
        //setupBackPressCallback(this)


        // init voiewmodel
        val repository = ZakatRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = ZakatViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_zakat_saved,container,false)
        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)

        customAlertDialog = CustomAlertDialog().getInstance()
        customAlertDialog?.setupDialog(
            callback = this@ZakatSavedFragment,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.cancel),
            btn2Text = localContext.getString(R.string.delete),
            titileText = localContext.getString(R.string.want_to_delete),
            subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_saved_history)
        )

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
            loadingState()
            loadApiData()
        }

        listView.apply {
            post {
                adapter = zakatSavedAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        loadApiData()
    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getSavedZakat(getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.savedZakatLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()

                CommonResource.EMPTY -> emptyState()
                is ZakatResource.savedZakatList -> viewState(it.data)
                is ZakatResource.historyDeleteFailed ->
                {
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()
                    requireContext().toast("Failed to delete history")
                }
                is ZakatResource.historyDeleted ->
                {
                    val btn2 = customAlertDialog?.getBtn2()
                    btn2?.isClickable = true
                    LoadingButton().getInstance(requireContext()).removeLoader()
                    customAlertDialog?.dismissDialog()
                    zakatSavedAdapter.delItem(adapterPosition)
                    if (zakatSavedAdapter.itemCount == 0)
                        emptyState()
                    requireContext().toast("History deleted successful")
                }

                is CommonResource.CLEAR -> Unit

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
        zakatSavedAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {
        val btn2 = customAlertDialog?.getBtn2()
        btn2?.isClickable = false
        btn2?.text = btn2?.let { LoadingButton().getInstance(requireContext()).loader(it) }

        lifecycleScope.launch {
            zakatHistoryData?.Id?.let { viewmodel.delHistory(it,adapterPosition) }
        }

    }

    override fun delHistory(data: Data, position: Int) {
        zakatHistoryData = data
        adapterPosition = position
        customAlertDialog?.showDialog(false)
    }

    override fun viewCalculation(data: Data) {

        val bundle = Bundle().apply {
            putParcelable("zakatData", data)
        }
        gotoFrag(R.id.action_zakatFragment_to_zakatCalculatorFragment,data = bundle)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            viewmodel.clear()
        }
    }

    override fun onBackPress() {

        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = Deen.msisdn,
                    pagename = "zakat",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }

}