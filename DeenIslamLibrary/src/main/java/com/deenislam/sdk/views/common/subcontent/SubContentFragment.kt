package com.deenislam.sdk.views.common.subcontent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.QurbaniCallback
import com.deenislam.sdk.service.callback.common.Common3DotMenuCallback
import com.deenislam.sdk.service.database.AppPreference
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.SubCatCardListResource
import com.deenislam.sdk.service.models.common.ContentSetting
import com.deenislam.sdk.service.models.common.ContentSettingResource
import com.deenislam.sdk.service.models.common.OptionList
import com.deenislam.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislam.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislam.sdk.service.repository.SubCatCardListRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.copyToClipboard
import com.deenislam.sdk.utils.htmlFormat
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.viewmodels.SubCatCardListViewModel
import com.deenislam.sdk.viewmodels.common.ContentSettingVMFactory
import com.deenislam.sdk.viewmodels.common.ContentSettingViewModel
import com.deenislam.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislam.sdk.views.adapters.qurbani.QurbaniCommonContentAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.deenislam.sdk.views.common.Common3DotMenu
import com.deenislam.sdk.views.common.CommonContentSetting
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


internal class SubContentFragment : BaseRegularFragment(),
    otherFagmentActionCallback,
PlayerCommonSelectionList.PlayerCommonSelectionListCallback,
    QurbaniCallback, Common3DotMenuCallback
{

    private lateinit var contentList: RecyclerView
    private lateinit var contentSettingViewModel: ContentSettingViewModel
    private lateinit var contentSetting: ContentSetting
    private lateinit var viewmodel: SubCatCardListViewModel
    private var firstload = false
    private val navArgs: SubContentFragmentArgs by navArgs()
    private lateinit var qurbaniCommonContentAdapter: QurbaniCommonContentAdapter

    override fun OnCreate() {
        super.OnCreate()

        val factory = ContentSettingVMFactory()
        contentSettingViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[ContentSettingViewModel::class.java]

        val repository = SubCatCardListRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = SubCatCardListViewModel(repository)

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
            viewmodel.getSubCatList(navArgs.categoryID,getLanguage(),navArgs.pageTag)
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }


    private fun initObserver(){

        viewmodel.subCatLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is SubCatCardListResource.SubcatList -> {
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
        CommonContentSetting.updateFontName(requireContext())
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

                   /* "share" -> {

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
                            copyToClipboard("$content\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")
                            toast("Content copied")
                        }

                    }
                }
            }
        }


        Common3DotMenu.closeDialog()
    }

}