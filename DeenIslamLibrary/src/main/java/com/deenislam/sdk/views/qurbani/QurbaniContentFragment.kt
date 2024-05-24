package com.deenislam.sdk.views.qurbani

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.models.common.ContentSettingResource
import com.deenislam.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.qurbani.getBanglaSize
import com.deenislam.sdk.viewmodels.common.ContentSettingVMFactory
import com.deenislam.sdk.viewmodels.common.ContentSettingViewModel
import com.deenislam.sdk.views.adapters.qurbani.QurbaniContentAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton

internal class QurbaniContentFragment : BaseRegularFragment() {

    private lateinit var title:AppCompatTextView
    private lateinit var hadithList:RecyclerView
    private lateinit var qurbaniContentAdapter: QurbaniContentAdapter
    private lateinit var bannerImg:AppCompatImageView
    private lateinit var countBtn:MaterialButton
    private lateinit var viewmodel: ContentSettingViewModel

    companion object {
        fun newInstance(cusarg: Bundle?): QurbaniContentFragment {
            val fragment = QurbaniContentFragment()
            fragment.arguments = cusarg
            return fragment
        }
    }

    override fun OnCreate() {
        super.OnCreate()

        val factory = ContentSettingVMFactory()
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[ContentSettingViewModel::class.java]

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_qurbani_content,container,false)

        title = mainview.findViewById(R.id.title)
        hadithList = mainview.findViewById(R.id.hadithList)
        bannerImg = mainview.findViewById(R.id.bannerImg)
        countBtn = mainview.findViewById(R.id.countBtn)
        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()

        arguments?.let {
            val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable("data", Data::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable("data") as? Data
            }

            countBtn.text = "${it.getInt("index",0)+1}/${it.getInt("total",0)}".numberLocale()

            if(data!=null){



                bannerImg.imageLoad(url = "$BASE_CONTENT_URL_SGP${data.ImageUrl}", placeholder_1_1 = true)


                title.text = data.Title


                if(data.details.isNullOrEmpty()){
                    hadithList.hide()
                }else{

                    if(!this::qurbaniContentAdapter.isInitialized)
                        qurbaniContentAdapter = QurbaniContentAdapter(data.details)

                    hadithList.apply {
                        adapter = qurbaniContentAdapter
                    }

                }

                baseViewState()

            }else
                baseEmptyState()
        }

    }

    private fun initObserver(){
        viewmodel.contentSettingLiveData.observe(viewLifecycleOwner){
            when(it){
                is ContentSettingResource.Update -> {

                    val banglaSize = it.contentSetting.banglaFontSize.getBanglaSize(20F)

                        title.apply {
                            setTextSize(
                                TypedValue.COMPLEX_UNIT_SP,banglaSize
                            )
                        }

                    if(this::qurbaniContentAdapter.isInitialized)
                    qurbaniContentAdapter.update()

                    }
                }
            }
        }


}