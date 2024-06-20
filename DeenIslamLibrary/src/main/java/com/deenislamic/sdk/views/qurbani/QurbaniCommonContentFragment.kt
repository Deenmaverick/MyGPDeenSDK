package com.deenislamic.sdk.views.qurbani

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.QurbaniCallback
import com.deenislamic.sdk.service.callback.common.Common3DotMenuCallback
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.QurbaniResource
import com.deenislamic.sdk.service.models.common.ContentSetting
import com.deenislamic.sdk.service.models.common.ContentSettingResource
import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.service.repository.QurbaniRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.copyToClipboard
import com.deenislamic.sdk.utils.htmlFormat
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.common.ContentSettingViewModel
import com.deenislamic.sdk.viewmodels.QurbaniViewModel
import com.deenislamic.sdk.viewmodels.common.ContentSettingVMFactory
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.adapters.qurbani.QurbaniCommonContentAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.deenislamic.sdk.views.common.Common3DotMenu
import com.deenislamic.sdk.views.common.CommonContentSetting
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


internal class QurbaniCommonContentFragment : BaseRegularFragment(),
    otherFagmentActionCallback,
    PlayerCommonSelectionList.PlayerCommonSelectionListCallback,
    QurbaniCallback, Common3DotMenuCallback
{

    private lateinit var contentList:RecyclerView
    private lateinit var contentSettingViewModel: ContentSettingViewModel
    private lateinit var contentSetting: ContentSetting
    private lateinit var viewmodel: QurbaniViewModel
    private var firstload = false
    private val navArgs:QurbaniCommonContentFragmentArgs by navArgs()
    private lateinit var qurbaniCommonContentAdapter: QurbaniCommonContentAdapter

    override fun OnCreate() {
        super.OnCreate()

        val repository = QurbaniRepository(
            deenService = NetworkProvider().getInstance().provideDeenService()
        )

        val factory = ContentSettingVMFactory()
        contentSettingViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[ContentSettingViewModel::class.java]

        viewmodel = QurbaniViewModel(repository)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_qurbani_common_content,container,false)

        contentList = mainview.findViewById(R.id.contentList)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = R.drawable.ic_settings,
            callback = this,
            actionnBartitle = navArgs.pageTitle,
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        CallBackProvider.setFragment(this)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentSetting = AppPreference.getContentSetting()
        CommonContentSetting.setup(contentSettingViewModel,lifecycleScope)

        initObserver()
        if(!firstload)
            loadapi()
        firstload = true
    }

    private fun loadapi(){
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getContentByCat(navArgs.data.SurahId,getLanguage())
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }


    private fun initObserver(){
        viewmodel.qurbaniLiveData.observe(viewLifecycleOwner){
            when(it){
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is QurbaniResource.QurbaniContentByCat ->{

                    if(!this::qurbaniCommonContentAdapter.isInitialized)
                    qurbaniCommonContentAdapter = QurbaniCommonContentAdapter(it.data)

                    contentList.apply {
                      adapter = qurbaniCommonContentAdapter
                    }

                    baseViewState()


                }
            }
        }

        contentSettingViewModel.contentSettingLiveData.observe(viewLifecycleOwner){
            when(it){
                is ContentSettingResource.Update -> {

                    if(this::qurbaniCommonContentAdapter.isInitialized)
                        qurbaniCommonContentAdapter.update()

                }
            }
        }
    }


    override fun action1() {

    }


    override fun action2() {
        CommonContentSetting.showDialog(requireContext(),localContext,localInflater)
    }

    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {

        contentSetting.arabicFont = data.Id
        AppPreference.setContentSetting(contentSetting)
        lifecycleScope.launch {
            contentSettingViewModel.update(contentSetting)
        }
        CommonContentSetting.closeDialog()
    }

    override fun qurbaniCommonContentClicked(absoluteAdapterPosition: Int, isExpanded: Boolean) {

        if(this::qurbaniCommonContentAdapter.isInitialized && !isExpanded){
            contentList.smoothScrollToPosition(absoluteAdapterPosition)
        }
    }

    override fun menu3dotClicked(getdata: Data) {

        val optionList3Dot = arrayListOf(
            OptionList("copy",R.drawable.ic_content_copy,localContext.getString(R.string.copy_content)),
            //OptionList("share",R.drawable.ic_share,localContext.getString(R.string.share))

        )

        context?.let { Common3DotMenu.showDialog(it,localInflater,getdata.Title.toString(),optionList3Dot,getdata) }

    }

    override fun <T> Common3DotMenuOptionClicked(getdata: OptionList, data: T) {

        when(data){

            is Data -> {

                when(getdata.type){

                    /*"share" -> {

                        var subContent = ""

                        data.details?.forEach {

                            if(it.Title.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${it.Title}"
                            if(it.Text.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${it.Text.htmlFormat()}"
                            if(it.TextInArabic.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${Jsoup.parse(it.TextInArabic).text()}"
                            if(it.Pronunciation.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${it.Pronunciation.htmlFormat()}"
                            if(it.reference.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${it.reference.htmlFormat()}"

                        }

                        val content  = "${data.Title}\n\n$subContent"


                        context?.shareLargeTextInChunks("$content\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")

                    }*/

                    "copy" -> {

                        var subContent = ""

                        data.details?.forEach {

                            if(it.Title.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${it.Title}"
                            if(it.Text.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${it.Text.htmlFormat()}"
                            if(it.TextInArabic.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${Jsoup.parse(it.TextInArabic).text()}"
                            if(it.Pronunciation.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${it.Pronunciation.htmlFormat()}"
                            if(it.reference.isNotEmpty())
                                subContent = "${if(subContent.isNotEmpty()) subContent+"\n" else subContent}${it.reference.htmlFormat()}"

                        }

                        val content  = "${data.Title}\n\n$subContent"


                        context?.apply {
                            copyToClipboard(content)
                            toast("Content copied")
                        }

                    }
                }
            }
        }


        Common3DotMenu.closeDialog()
    }

}