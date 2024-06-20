package com.deenislamic.sdk.views.islamifazael

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamiFazaelCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamiFazaelResource
import com.deenislamic.sdk.service.network.response.islamifazael.Data
import com.deenislamic.sdk.service.repository.IslamiFazaelRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.IslamiFazaelViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.adapters.islamifazael.IslamiFazaelListAdapter
import kotlinx.coroutines.launch


internal class IslamiFazaelFragment : BaseRegularFragment(), IslamiFazaelCallback {

    private lateinit var listview: RecyclerView
    private lateinit var viewmodel: IslamiFazaelViewModel

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

        // init viewmodel
        val repository = IslamiFazaelRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = IslamiFazaelViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_islami_fazael,container,false)

        listview = mainview.findViewById(R.id.listview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.fazael_and_masael),
            backEnable = true,
            view = mainview
        )

        CallBackProvider.setFragment(this)

        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_fazael",
                    trackingID = getTrackingID()
                )
            }
        }


            loadpage()
    }


    private fun loadpage(){

        initObserver()

        if(!firstload)
            loadapi()
        firstload = true
    }

    private fun initObserver(){

        viewmodel.islamiFazaelLivedata.observe(viewLifecycleOwner){

            when(it){
                is CommonResource.EMPTY -> baseEmptyState()
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()

                is IslamiFazaelResource.AllFazaelList ->{
                    listview.apply {
                        adapter = IslamiFazaelListAdapter(it.data)
                    }
                    baseViewState()
                }
            }
        }
    }

    private fun loadapi(){
        lifecycleScope.launch {
            viewmodel.getAllIslamiFazael(getLanguage())
        }
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_fazael",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }
    }

    override fun FazaelCatClicked(getdata: Data) {
        val bundle = Bundle()
        bundle.putString("title",getdata.category)
        bundle.putInt("catid",getdata.Id)
        gotoFrag(R.id.action_global_fazaelByCatFragment,bundle)
    }
}