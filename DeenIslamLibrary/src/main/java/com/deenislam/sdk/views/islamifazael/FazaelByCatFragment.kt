package com.deenislam.sdk.views.islamifazael

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.IslamiFazaelCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamiFazaelResource
import com.deenislam.sdk.service.network.response.islamifazael.bycat.FazaelDataItem
import com.deenislam.sdk.service.repository.IslamiFazaelRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.IslamiFazaelViewModel
import com.deenislam.sdk.views.adapters.islamifazael.IslamiFazaelByCatAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class FazaelByCatFragment : BaseRegularFragment(), IslamiFazaelCallback {

    private lateinit var listview: RecyclerView
    private lateinit var viewmodel: IslamiFazaelViewModel
    private val navArgs:FazaelByCatFragmentArgs by navArgs()

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
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
        val mainview = localInflater.inflate(R.layout.fragment_fazael_by_cat,container,false)

        listview = mainview.findViewById(R.id.listview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = navArgs.title,
            backEnable = true,
            view = mainview
        )

        CallBackProvider.setFragment(this)


        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else
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

                is IslamiFazaelResource.FazaelByCat ->{
                    listview.apply {
                        adapter = IslamiFazaelByCatAdapter(it.data)
                    }
                    baseViewState()
                }
            }
        }
    }

    private fun loadapi(){
        lifecycleScope.launch {
            viewmodel.getFazaelByCat(getLanguage(),navArgs.catid)
        }
    }

    override fun FazaelShareClicked(getdata: FazaelDataItem){
        /*val shareText = if(getdata.textinarabic.isNotEmpty()) "${getdata.textinarabic.removeHtmlTags()}\n\n" else ""+
                "${getdata.text.removeHtmlTags()}\n\n"+
                if(getdata.reference.isNotEmpty()) "${getdata.reference.removeHtmlTags()}\n\n" else ""+
                        "Explore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6"

        context?.shareText(shareText)*/
    }
}