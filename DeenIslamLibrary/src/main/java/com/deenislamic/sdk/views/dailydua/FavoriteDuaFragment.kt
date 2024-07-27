package com.deenislamic.sdk.views.dailydua

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.Common3DotMenuCallback
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.alertdialog.CustomAlertDialog
import com.deenislamic.sdk.service.libs.alertdialog.CustomDialogCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.DailyDuaResource
import com.deenislamic.sdk.service.models.common.ContentSetting
import com.deenislamic.sdk.service.models.common.ContentSettingResource
import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.network.response.dailydua.favdua.Data
import com.deenislamic.sdk.service.repository.DailyDuaRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.LoadingButton
import com.deenislamic.sdk.utils.copyToClipboard
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.htmlFormat
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.DailyDuaViewModel
import com.deenislamic.sdk.viewmodels.common.ContentSettingVMFactory
import com.deenislamic.sdk.viewmodels.common.ContentSettingViewModel
import com.deenislamic.sdk.views.adapters.dailydua.FavDuaAdapterCallback
import com.deenislamic.sdk.views.adapters.dailydua.FavoriteDuaAdapter
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.deenislamic.sdk.views.common.Common3DotMenu
import com.deenislamic.sdk.views.common.CommonContentSetting
import com.deenislamic.sdk.views.myfavorites.MyFavoritesFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

internal class FavoriteDuaFragment(
    private val checkFirstload: Boolean = false
) : BaseRegularFragment(),
    FavDuaAdapterCallback,
    CustomDialogCallback,
    Common3DotMenuCallback,
    PlayerCommonSelectionList.PlayerCommonSelectionListCallback,
otherFagmentActionCallback{

    private lateinit var listView:RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var nodataLayout: NestedScrollView
    private lateinit var noInternetLayout: NestedScrollView
    private lateinit var noInternetRetry: MaterialButton
    private lateinit var contentSettingViewModel: ContentSettingViewModel
    private lateinit var contentSetting: ContentSetting

    private var customAlertDialog: CustomAlertDialog? =null

    private var duaID:Int = 0
    private var adapterPosition:Int = 0

    private lateinit var favoriteDuaAdapter: FavoriteDuaAdapter

    private lateinit var viewmodel:DailyDuaViewModel

    private var firstload:Boolean = false

    override fun OnCreate() {
        super.OnCreate()

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
        val mainView = localInflater.inflate(R.layout.fragment_daily_dua_page,container,false)
        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        nodataLayout = mainView.findViewById(R.id.nodataLayout)
        noInternetLayout = mainView.findViewById(R.id.no_internet_layout)
        noInternetRetry = noInternetLayout.findViewById(R.id.no_internet_retry)

        customAlertDialog = CustomAlertDialog().getInstance()
        /*customAlertDialog?.setupDialog(
            callback = this@FavoriteDuaFragment,
            context = requireContext(),
            btn1Text = localContext.getString(R.string.cancel),
            btn2Text = localContext.getString(R.string.delete),
            titileText = localContext.getString(R.string.want_to_delete),
            subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
        )*/

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentSetting = AppPreference.getContentSetting()
        CommonContentSetting.setup(contentSettingViewModel,lifecycleScope)

        loadPage()
    }


   /* override fun onResume() {
        super.onResume()

    }*/

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "daily_dua",
                    trackingID = getTrackingID()
                )
            }
        }
        tryCatch { super.onBackPress() }

    }
    private fun loadPage()
    {

        ViewCompat.setTranslationZ(progressLayout, 10F)
        ViewCompat.setTranslationZ(noInternetLayout, 10F)
        ViewCompat.setTranslationZ(nodataLayout, 10F)

        initObserver()


        favoriteDuaAdapter = FavoriteDuaAdapter(this@FavoriteDuaFragment)

        listView.apply {

                val margins = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    leftMargin = 16.dp
                    rightMargin = 16.dp
                }
                layoutParams = margins

                adapter = favoriteDuaAdapter

                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

    }


    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(menuVisible){
            CallBackProvider.setFragment(this)
            setupActionBar()
            if(checkFirstload && !firstload) {
                baseLoadingState()
                loadApiData()
            }else if(!checkFirstload)
                loadApiData()

            firstload = true

            customAlertDialog?.setupDialog(
                callback = this@FavoriteDuaFragment,
                context = requireContext(),
                btn1Text = localContext.getString(R.string.cancel),
                btn2Text = localContext.getString(R.string.delete),
                titileText = localContext.getString(R.string.want_to_delete),
                subTitileText = localContext.getString(R.string.do_you_want_to_remove_this_favorite)
            )

        }
    }


    fun setupActionBar() {

        when (parentFragment) {
            is DailyDuaFragment -> {
                (parentFragment as DailyDuaFragment?)?.getActionBar()?.let {
                    it.show()
                    setupActionForOtherFragment(R.drawable.ic_settings,0,this,localContext.getString(R.string.daily_dua),true,it)
                }
            }

            is MyFavoritesFragment -> {
                (parentFragment as MyFavoritesFragment?)?.getActionBar()?.let {
                    it.show()
                    setupActionForOtherFragment(R.drawable.ic_settings,0,this,localContext.getString(R.string.daily_dua),true,it)
                }
            }
            else -> {
                // Handle other cases or do nothing
            }
        }

    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewmodel.getFavDua(getLanguage())
        }
    }

    private fun initObserver()
    {
        viewmodel.favDuaLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.API_CALL_FAILED -> noInternetState()

                CommonResource.EMPTY -> emptyState()
                is DailyDuaResource.favDuaList -> viewState(it.data)

            }
        }

        viewmodel.duaPreviewLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                CommonResource.ACTION_API_CALL_FAILED ->
                {
                    customAlertDialog?.dismissDialog()
                    requireContext().toast(localContext.getString(R.string.something_went_wrong_try_again))
                }

                is DailyDuaResource.setFavDua ->
                {
                    favoriteDuaAdapter.delItem(it.position)

                    listView.post {
                        if(favoriteDuaAdapter.itemCount <= 0)
                            emptyState()
                    }
                    customAlertDialog?.dismissDialog()

                }
            }
        }

        contentSettingViewModel.contentSettingLiveData.observe(viewLifecycleOwner){
            when(it){
                is ContentSettingResource.Update -> {

                    if(this::favoriteDuaAdapter.isInitialized) {
                        favoriteDuaAdapter.update()
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
            favoriteDuaAdapter.notifyItemChanged(position)
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
        favoriteDuaAdapter.update(data)

        listView.post {
            progressLayout.hide()
            nodataLayout.hide()
            noInternetLayout.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            viewmodel.clearFavDuaLiveData()
        }
    }

    override fun favClick(duaID: Int, position: Int) {
        this.duaID = duaID
        adapterPosition = position
        customAlertDialog?.showDialog(false)
    }

    override fun duaClick(duaId: Int, subCategory: Int, category: String) {

        val bundle = Bundle().apply {
            putInt("category", subCategory)
            putString("catName", category)
            putInt("duaID", duaId)
        }
        gotoFrag(R.id.action_global_allDuaPreviewFragment,data = bundle)
    }

    override fun dotMenuClicked(dua: Data) {

        val optionList3Dot = arrayListOf(
            OptionList("copy",R.drawable.ic_content_copy,localContext.getString(R.string.copy_content)),
            OptionList("share",R.drawable.ic_share,localContext.getString(R.string.share))
        )

        context?.let { Common3DotMenu.showDialog(it,localInflater,dua.Title,optionList3Dot,dua) }
    }

    override fun clickBtn1() {
        customAlertDialog?.dismissDialog()
    }

    override fun clickBtn2() {
        val btn2 = customAlertDialog?.getBtn2()
        btn2?.text = btn2?.let { LoadingButton().getInstance(requireContext()).loader(it) }

        lifecycleScope.launch {
            viewmodel.setFavDua(isFavorite = true, duaId = duaID, language = getLanguage(),adapterPosition)
        }
    }

    private fun shareDua(enText: String, bnText: String, arText: String, dua: Data) {
        val bundle = Bundle()
        bundle.putString("enText", Jsoup.parse(enText).text())
        bundle.putString("bnText", Jsoup.parse(bnText).text())
        bundle.putString("arText", Jsoup.parse(arText).text())
        bundle.putString("title",dua.Title)
        bundle.putString("heading",dua.Title)
        gotoFrag(R.id.action_global_shareFragment,bundle)
    }

    override fun contentClicked(absoluteAdapterPosition: Int, isExpanded: Boolean) {
        if(this::favoriteDuaAdapter.isInitialized && !isExpanded){
            listView.smoothScrollToPosition(absoluteAdapterPosition)
        }
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
                            copyToClipboard("$content")
                            toast("Content copied")
                        }

                    }
                }
            }
        }


        Common3DotMenu.closeDialog()
    }

    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {

    }

    override fun action1() {
        CommonContentSetting.showDialog(requireContext(),localContext,localInflater)
    }

    override fun action2() {
    }

}
