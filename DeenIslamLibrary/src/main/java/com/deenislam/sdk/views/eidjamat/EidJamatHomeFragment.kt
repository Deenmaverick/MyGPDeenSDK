package com.deenislam.sdk.views.eidjamat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.EidJamatCallback
import com.deenislam.sdk.service.callback.RamadanCallback
import com.deenislam.sdk.service.database.AppPreference
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamicNameResource
import com.deenislam.sdk.service.models.ramadan.StateModel
import com.deenislam.sdk.service.network.response.eidjamat.EidJamatListResponse
import com.deenislam.sdk.service.repository.IslamicNameRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.loadHtmlFromAssets
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.viewmodels.IslamicNameViewModel
import com.deenislam.sdk.views.adapters.EidJamatAdapter
import com.deenislam.sdk.views.adapters.common.CommonStateList
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch


internal class EidJamatHomeFragment : BaseRegularFragment(), EidJamatCallback, RamadanCallback {

    private lateinit var viewmodel: IslamicNameViewModel
    private lateinit var listview: RecyclerView
    private lateinit var locationLayout: MaterialCardView
    private lateinit var locationTxt: AppCompatTextView
    private var commonStateList: CommonStateList? = null
    private lateinit var clLocation: ConstraintLayout
    private var district = "dhaka"
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

        //init viewmodel
        val repository = IslamicNameRepository(
            islamicNameService = NetworkProvider().getInstance().provideIslamicNameService()
        )
        viewmodel = IslamicNameViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_eid_jamat_home, container, false)
        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.title_eid_jamat),
            backEnable = true,
            view = mainview
        )

        listview = mainview.findViewById(R.id.listview)
        locationLayout = mainview.findViewById(R.id.locationLayout)
        locationTxt = mainview.findViewById(R.id.locationTxt)
        clLocation = mainview.findViewById(R.id.clLocation)


        setupCommonLayout(mainview)
        CallBackProvider.setFragment(this)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "eid_jamat",
                    trackingID = getTrackingID()
                )
            }
        }

        firstload = true

        commonStateList = CommonStateList(clLocation)

        loadpage(district)

    }


    override fun noInternetRetryClicked() {
        loadpage(district)
    }

    private fun initObserver() {
        viewmodel.eidJamatLiveData.observe(viewLifecycleOwner) {
            when (it) {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()

                CommonResource.EMPTY -> baseEmptyState()
                is IslamicNameResource.eidJamatList -> viewState(it.data)
            }
        }
    }

    private fun viewState(data: List<EidJamatListResponse.Data>) {
        listview.apply {
            adapter = EidJamatAdapter(data)
        }

        baseViewState()
    }

    private fun loadpage(district: String) {
        initObserver()
        baseLoadingState()
        loadApi(district)
    }

    private fun loadApi(district: String) {

        lifecycleScope.launch {

            viewmodel.getEidJamatList(district)
        }
    }


    override fun mosqueDirectionClicked(data: EidJamatListResponse.Data) {
        val url = "https://maps.google.com/maps?saddr=" +
                AppPreference.getUserCurrentLocation().lat +
                "," + AppPreference.getUserCurrentLocation().lng +
                "&daddr=" + data.latitude + "," +
                data.longitude + "&output=embed"

        val getHtml = requireContext().loadHtmlFromAssets("nearest_mosque_map.html")
            .replace("#URL#", url)

        val bundle = Bundle()
        bundle.putString("title", data.title)
        bundle.putString("webpage", getHtml)
        gotoFrag(R.id.action_global_basicWebViewFragment, bundle)
    }




    override fun stateSelected(stateModel: StateModel) {
        locationTxt.text = stateModel.stateValue
        commonStateList?.stateSelected(stateModel)
        loadpage(stateModel.state)
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "eid_jamat",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }
}