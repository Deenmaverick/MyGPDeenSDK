package com.deenislamic.sdk.views.common.subcatcardlist

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.DashboardPatchCallback
import com.deenislamic.sdk.service.callback.QurbaniCallback
import com.deenislamic.sdk.service.callback.SurahCallback
import com.deenislamic.sdk.service.callback.common.BasicCardListCallback
import com.deenislamic.sdk.service.callback.common.Common3DotMenuCallback
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.ImageViewPopupDialog
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.SubCatCardListResource
import com.deenislamic.sdk.service.models.common.ContentSetting
import com.deenislamic.sdk.service.models.common.ContentSettingResource
import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.repository.SubCatCardListRepository
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.MENU_PRAYER_LEARNING
import com.deenislamic.sdk.utils.copyToClipboard
import com.deenislamic.sdk.utils.htmlFormat
import com.deenislamic.sdk.utils.shareImage
import com.deenislamic.sdk.utils.shareLargeTextInChunks
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.SubCatCardListViewModel
import com.deenislamic.sdk.viewmodels.common.ContentSettingVMFactory
import com.deenislamic.sdk.viewmodels.common.ContentSettingViewModel
import com.deenislamic.sdk.views.adapters.common.SubCatPatchAdapter
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.deenislamic.sdk.views.common.Common3DotMenu
import com.deenislamic.sdk.views.common.CommonContentSetting
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


internal class SubCatPatchFragment : BaseRegularFragment(),
    BasicCardListCallback,
    HorizontalCardListCallback,
    DashboardPatchCallback,
    SurahCallback,
    otherFagmentActionCallback,
    PlayerCommonSelectionList.PlayerCommonSelectionListCallback,
    QurbaniCallback,
    Common3DotMenuCallback {

    private val navArgs: SubCatPatchFragmentArgs by navArgs()
    private lateinit var viewmodel: SubCatCardListViewModel
    private lateinit var basicCardListAdapter: SubCatPatchAdapter
    private lateinit var listView: RecyclerView
    private var firstload = false
    private var patchdata:List<Data> ? = null
    private lateinit var contentSettingViewModel: ContentSettingViewModel
    private lateinit var contentSetting: ContentSetting

    override fun OnCreate() {
        super.OnCreate()

        // init viewmodel

        val repository = SubCatCardListRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = SubCatCardListViewModel(repository)


        val factory = ContentSettingVMFactory()
        contentSettingViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[ContentSettingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_sub_cat_card_list,container,false)

        listView = mainview.findViewById(R.id.listView)

        listView.apply {
            itemAnimator = null
        }

        setupActionForOtherFragment(
            action1 = 0,
            action2 = R.drawable.ic_settings,
            callback = this,
            actionnBartitle = navArgs.pageTitle,
            backEnable = true,
            view = mainview
        )
        CallBackProvider.setFragment(this)
        setupCommonLayout(mainview)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentSetting = AppPreference.getContentSetting()
        CommonContentSetting.setup(contentSettingViewModel,lifecycleScope)

        initObserver()
        if(!firstload)
            loadApi()
        firstload = true
    }

    override fun noInternetRetryClicked() {
        super.noInternetRetryClicked()
        loadApi()
    }

    private fun loadApi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getSubCatPatch(navArgs.categoryID,getLanguage(),navArgs.pageTag,navArgs.contentType,navArgs.subid)
        }
    }

    private fun initObserver()
    {
        viewmodel.subCatLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is SubCatCardListResource.SubcatListPatch -> viewState(it.data)
            }
        }

        contentSettingViewModel.contentSettingLiveData.observe(viewLifecycleOwner){
            when(it){
                is ContentSettingResource.Update -> {

                    if(this::basicCardListAdapter.isInitialized) {
                        //basicCardListAdapter.updateRichContent()
                        listView.post {
                            updateAyatAdapterVisibleItems()
                        }
                    }

                }
            }
        }
    }

    private fun updateAyatAdapterVisibleItems() {
        val layoutManager = listView.layoutManager as? LinearLayoutManager ?: return

        // Find the first and last visible item positions
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        // Check if there are no visible items
        if (firstVisiblePosition == RecyclerView.NO_POSITION || lastVisiblePosition == RecyclerView.NO_POSITION) {
            return
        }

        // Calculate the range of items to update
        val itemCount = layoutManager.itemCount
        val startUpdateIndex = Math.max(0, firstVisiblePosition - 5)
        val endUpdateIndex = Math.min(itemCount - 1, lastVisiblePosition + 5)

        // Iterate through the range and update each item if the position is valid
        for (position in startUpdateIndex..endUpdateIndex) {
            if (position in 0 until itemCount) {
                basicCardListAdapter.notifyItemChanged(position)
            }
        }
    }


    override fun action1() {

    }


    override fun action2() {
        if(this::basicCardListAdapter.isInitialized)
            CommonContentSetting.showDialog(requireContext(),localContext,localInflater)
    }

    private fun viewState(data: List<Data>)
    {

        patchdata = data

        if(!this::basicCardListAdapter.isInitialized)
        basicCardListAdapter = SubCatPatchAdapter(data)

        listView.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            adapter = basicCardListAdapter
        }

        baseViewState()

    }

    override fun patchItemClicked(getData: Item) {

        when(getData.ContentType){
            "menu" -> {

                val bundle = Bundle()
                    bundle.putInt("categoryID", getData.SurahId)
                    bundle.putString("pageTitle",getData.ArabicText)
                    bundle.putString("pageTag", MENU_PRAYER_LEARNING)
                    gotoFrag(R.id.action_global_subContentFragment,bundle)
            }


            "ptub" -> {

                patchdata?.let {

                    var dataIndex = -1
                    var itemIndex = 0

                    it.forEachIndexed { index, dataValue ->
                        dataValue.Items.forEachIndexed { innerIndex, item ->
                            // Replace with the condition to check if the items match
                            if (item.ContentType == getData.ContentType && item.Id == getData.Id) {
                                dataIndex = index
                                itemIndex = innerIndex
                                return@forEachIndexed
                            }
                        }
                    }

                    if(dataIndex !=-1) {
                        val bundle = Bundle()
                        bundle.putParcelable("selectedData", getData)
                        bundle.putParcelableArray("data", it[dataIndex].Items.toTypedArray())
                        bundle.putBoolean("isHome",false)
                        gotoFrag(R.id.action_global_youtubeVideoFragment, bundle)
                    }

                }
            }

            "sd" ->{

                val bundle = Bundle().apply {
                    putInt("surahID", getData.SurahId)
                    putString("surahName", getData.ArabicText)
                    //putParcelable("suraList", SurahList(chapters = surahList))
                }

                gotoFrag(R.id.action_global_alQuranFragment,data = bundle)
            }

        }

    }

    override fun dashboardPatchClickd(patch: String, data: Item?) {

        when(patch){
            "Dua" -> {
                //changeMainViewPager(2)

                data?.let {
                    val bundle = Bundle()
                    bundle.putString("title",it.FeatureTitle)
                    bundle.putString("imgUrl","$BASE_CONTENT_URL_SGP${it.imageurl1}")
                    //bundle.putString("content","${getdata.Title}:\n\n${getdata.Text}\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")
                    ImageViewPopupDialog.display(childFragmentManager,bundle)

                }
            }
        }
    }



    override fun surahClick(surahListData: com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data) {

        val bundle = Bundle().apply {
            putInt("surahID", surahListData.SurahId)
            putString("surahName", surahListData.SurahName)
            //putParcelable("suraList", SurahList(chapters = surahList))
        }

        gotoFrag(R.id.action_global_alQuranFragment,data = bundle)
    }

    override fun shareImage(bitmap: Bitmap) {
        context?.shareImage(bitmap)
    }


    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {

        contentSetting = AppPreference.getContentSetting()
        contentSetting.arabicFont = data.Id
        AppPreference.setContentSetting(contentSetting)
        lifecycleScope.launch {
            contentSettingViewModel.update(contentSetting)
        }
        CommonContentSetting.updateFontName(requireContext())
    }

    override fun qurbaniCommonContentClicked(absoluteAdapterPosition: Int, isExpanded: Boolean) {

        Log.e("qurbaniCommonCont",absoluteAdapterPosition.toString())
        if(this::basicCardListAdapter.isInitialized && !isExpanded){
            basicCardListAdapter.updateRichContent(absoluteAdapterPosition,isExpanded)
            //listView.smoothScrollToPosition(absoluteAdapterPosition)
            listView.post {
                updateAyatAdapterVisibleItems()
            }
        }
    }



    override fun menu3dotClicked(getdata: com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data) {

        val optionList3Dot = arrayListOf(
            OptionList("copy",R.drawable.ic_content_copy,localContext.getString(R.string.copy_content)),
            OptionList("share",R.drawable.ic_share,localContext.getString(R.string.share))

        )

        context?.let { Common3DotMenu.showDialog(it,localInflater,getdata.Title.toString(),optionList3Dot,getdata) }

    }

    override fun <T> Common3DotMenuOptionClicked(getdata: OptionList, data: T) {

        when(data){

            is com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data -> {

                when(getdata.type){

                    "share" -> {

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


                        context?.shareLargeTextInChunks("$content")

                    }

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
                            copyToClipboard("$content")
                            toast("Content copied")
                        }

                    }
                }
            }
        }


        Common3DotMenu.closeDialog()
    }

}