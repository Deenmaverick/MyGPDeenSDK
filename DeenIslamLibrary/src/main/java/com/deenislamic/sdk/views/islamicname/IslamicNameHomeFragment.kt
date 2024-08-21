package com.deenislamic.sdk.views.islamicname

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.islamicname.IslamicNameCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicNameResource
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameHomeResponse
import com.deenislamic.sdk.service.repository.IslamicNameRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.getDrawable
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.IslamicNameViewModel
import com.deenislamic.sdk.views.adapters.islamicname.IslamicNameHomeAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.coroutines.launch


internal class IslamicNameHomeFragment : BaseRegularFragment(), otherFagmentActionCallback,
    IslamicNameCallback {

    private lateinit var boyLayout: MaterialCardView
    private lateinit var girlLayout: MaterialCardView
    private var firstload = false
    private lateinit var viewmodel: IslamicNameViewModel
    private lateinit var rvIslamicNamePatch: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var boyAvatar:AppCompatImageView
    private lateinit var girlAvatar:AppCompatImageView

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
        val repository = IslamicNameRepository(NetworkProvider().getInstance().provideIslamicNameService())
        viewmodel = IslamicNameViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_islamic_name_home, container, false)
        setupActionForOtherFragment(
            action1 = 0,
            action2 = R.drawable.ic_fav_quran,
            action3 = 0,
            callback = this@IslamicNameHomeFragment,
            actionnBartitle = localContext.getString(R.string.islamic_name),
            backEnable = true,
            view = mainView,
            actionIconColor = R.color.deen_txt_black_deep
        )
        CallBackProvider.setFragment(this)
        //init view
        boyLayout = mainView.findViewById(R.id.boyLayout)
        girlLayout = mainView.findViewById(R.id.girlLayout)
        rvIslamicNamePatch = mainView.findViewById(R.id.rvIslamicNamePatch)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        boyAvatar = mainView.findViewById(R.id.boyAvatar)
        girlAvatar = mainView.findViewById(R.id.girlAvatar)
        setupCommonLayout(mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boyAvatar.imageLoad(url = "ic_islamic_name_boy_avatar.png".getDrawable())
        girlAvatar.imageLoad(url = "ic_islamioc_name_girl_avatar.png".getDrawable())

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_name",
                    trackingID = getTrackingID()
                )
            }
        }

        boyLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString("gender", "male")
                putString("pageTitle", localContext.getString(R.string.muslim_boys_names))
            }
            gotoFrag(R.id.action_global_islamicNameFragment,bundle)
        }

        girlLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString("gender", "female")
                putString("pageTitle", localContext.getString(R.string.muslim_girls_names))
            }
            gotoFrag(R.id.action_global_islamicNameFragment, bundle)
        }

        initObserver()
        baseLoadingState()
        if(!firstload)
        loadApi()
        firstload = true

    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_name",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun initObserver() {
        viewmodel.islamicNamesHomeLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseViewState()
                is IslamicNameResource.islamicNamesPatch -> {

                    viewState(it.data[0].Items)
                }
            }
        }
    }

    private fun viewState(data: List<IslamicNameHomeResponse.Data.Item>) {

        rvIslamicNamePatch.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            adapter = IslamicNameHomeAdapter(data)
        }
        baseViewState()
    }

    private fun loadApi() {
        lifecycleScope.launch {
            viewmodel.getNamesHomePatch(getLanguage())
        }
    }

    override fun action1() {
    }

    override fun action2() {

        /*if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }*/
        gotoFrag(R.id.action_islamicNameFragment_to_islamicNameFavFargment)
    }

    override fun onItemClick(data: IslamicNameHomeResponse.Data.Item) {
        val bundle = Bundle()
        bundle.putParcelable("dataName",data)
        gotoFrag(R.id.action_global_subCatBasicCardDetailsFragment,bundle)
    }
}