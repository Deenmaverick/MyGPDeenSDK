package com.deenislamic.sdk.views.dailydua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.Common3DotMenuCallback
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.DailyDuaResource
import com.deenislamic.sdk.service.models.common.ContentSetting
import com.deenislamic.sdk.service.models.common.ContentSettingResource
import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.network.response.dailydua.duabycategory.Data
import com.deenislamic.sdk.service.repository.DailyDuaRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.copyToClipboard
import com.deenislamic.sdk.utils.htmlFormat
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.DailyDuaViewModel
import com.deenislamic.sdk.viewmodels.common.ContentSettingVMFactory
import com.deenislamic.sdk.viewmodels.common.ContentSettingViewModel
import com.deenislamic.sdk.views.adapters.dailydua.DuaByCatAdapter
import com.deenislamic.sdk.views.adapters.dailydua.DuaByCatCallback
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.deenislamic.sdk.views.common.Common3DotMenu
import com.deenislamic.sdk.views.common.CommonContentSetting
import kotlinx.coroutines.launch

internal class AllDuaPreviewFragment : BaseRegularFragment(),
    DuaByCatCallback,
    otherFagmentActionCallback,
    Common3DotMenuCallback, PlayerCommonSelectionList.PlayerCommonSelectionListCallback{

    private lateinit var listView: RecyclerView

    private lateinit var duaByCatAdapter: DuaByCatAdapter

    private lateinit var viewmodel: DailyDuaViewModel
    private val args: AllDuaPreviewFragmentArgs by navArgs()
    private lateinit var contentSettingViewModel: ContentSettingViewModel
    private lateinit var contentSetting: ContentSetting

    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)

        // init viewmodel
        val repository = DailyDuaRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = DailyDuaViewModel(repository)

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

        val mainView = localInflater.inflate(R.layout.fragment_all_dua_preview,container,false)
        //init view
        listView = mainView.findViewById(R.id.listView)

        setupActionForOtherFragment(0,R.drawable.ic_settings,this,args.catName,true,mainView)
        setupCommonLayout(mainView)
        CallBackProvider.setFragment(this)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentSetting = AppPreference.getContentSetting()
        CommonContentSetting.setup(contentSettingViewModel,lifecycleScope)

        loadPage()

    }

    private fun loadPage()
    {

        duaByCatAdapter = DuaByCatAdapter(this@AllDuaPreviewFragment)

        initObserver()

        baseLoadingState()


        listView.apply {
            post {
                adapter = duaByCatAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }

    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }

    override fun onResume() {
        super.onResume()
        if(!firstload)
            loadApiData()

        firstload = true
    }


    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getDuaByCategory(args.category,getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.duaPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                CommonResource.EMPTY -> baseEmptyState()
                is DailyDuaResource.duaPreview -> viewState(it.data)
                is DailyDuaResource.setFavDua -> updateFavorite(it.position,it.fav)
            }
        }

        contentSettingViewModel.contentSettingLiveData.observe(viewLifecycleOwner){
            when(it){
                is ContentSettingResource.Update -> {

                    if(this::duaByCatAdapter.isInitialized) {
                        duaByCatAdapter.update()
                        listView.post {
                            updateAyatAdapterVisibleItems()
                        }
                    }

                }
            }
        }
    }


    private fun updateAyatAdapterVisibleItems() {
        val layoutManager = listView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            // Check if you want to update this item
            duaByCatAdapter.notifyItemChanged(position)
        }
    }

    private fun viewState(data: List<Data>)
    {
        duaByCatAdapter.update(data)
        listView.post {
            if(args.duaID>0)
            {
                var adpos = -1
                data.forEach {
                    adpos++
                    if(it.DuaId == args.duaID)
                        listView.scrollToPosition(adpos)
                }
            }

            baseViewState()
        }

    }

    private fun updateFavorite(position: Int, fav: Boolean)
    {
        duaByCatAdapter.update(position,fav)
    }

    override fun favDua(isFavorite: Boolean, duaId: Int, position: Int) {
        if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }
        lifecycleScope.launch {
            viewmodel.setFavDua(isFavorite,duaId,getLanguage(),position)
        }
    }

    override fun shareDua(enText: String, bnText: String, arText: String, dua: Data) {
        /*val bundle = Bundle()
        bundle.putString("enText", Jsoup.parse(enText).text())
        bundle.putString("bnText", Jsoup.parse(bnText).text())
        bundle.putString("arText", Jsoup.parse(arText).text())
        bundle.putString("title",dua.Title)
        bundle.putString("heading",dua.Title)
        gotoFrag(R.id.action_global_shareFragment,bundle)*/
    }

    override fun contentClicked(absoluteAdapterPosition: Int, isExpanded: Boolean) {
        if(this::duaByCatAdapter.isInitialized && !isExpanded){
            listView.smoothScrollToPosition(absoluteAdapterPosition)
        }
    }

    override fun dotMenuClicked(dua: Data) {

        val optionList3Dot = arrayListOf(
            OptionList("copy",R.drawable.ic_content_copy,localContext.getString(R.string.copy_content)),
            OptionList("share",R.drawable.ic_share,localContext.getString(R.string.share))
        )

        context?.let { Common3DotMenu.showDialog(it,localInflater,dua.Title,optionList3Dot,dua) }
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
        CommonContentSetting.updateFontName(localContext)
    }

    override fun <T> Common3DotMenuOptionClicked(getdata: OptionList, data: T) {

        when(data){

            is Data -> {

                when(getdata.type){

                    "share" -> {

                        if(getLanguage() == "en")
                            shareDua(data.Transliteration,"",data.TextInArabic,data)
                        else
                            shareDua("",data.Transliteration,data.TextInArabic,data)

                    }

                    "copy" -> {

                        val part1 = "${localContext.getString(R.string.pronunciation_html)}${data.Transliteration}".htmlFormat()
                        val part2 = "${localContext.getString(R.string.meaning_html)}${data.Text}".htmlFormat()


                        val content  = "${data.Title}\n${data.TextInArabic}\n$part1\n\n$part2\n\n${data.Source.htmlFormat()}"

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