package com.deenislam.sdk.views.qurbani

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.AlQuranAyatCallback
import com.deenislam.sdk.service.callback.QurbaniCallback
import com.deenislam.sdk.service.database.AppPreference
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.ImageViewPopupDialog
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.QurbaniResource
import com.deenislam.sdk.service.models.common.ContentSetting
import com.deenislam.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislam.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislam.sdk.service.repository.QurbaniRepository
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.viewmodels.common.ContentSettingViewModel
import com.deenislam.sdk.viewmodels.QurbaniViewModel
import com.deenislam.sdk.viewmodels.common.ContentSettingVMFactory
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


internal class QurbaniDetailsFragment : BaseRegularFragment(), QurbaniCallback,otherFagmentActionCallback,
    AlQuranAyatCallback, PlayerCommonSelectionList.PlayerCommonSelectionListCallback {

    private lateinit var _viewpager:ViewPager2
    private lateinit var actionbar: ConstraintLayout
    private val mPageDestination: ArrayList<Fragment> = arrayListOf()
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter

    private lateinit var viewmodel: QurbaniViewModel
    private lateinit var contentSettingViewModel: ContentSettingViewModel

    private val navArgs:QurbaniDetailsFragmentArgs by navArgs()

    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View

    private var data:List<Data> ? = null

    private lateinit var prevStep: MaterialButton
    private lateinit var NextStep: MaterialButton

    private lateinit var contentSetting: ContentSetting
    private lateinit var arabicFontCommonSelectionList: PlayerCommonSelectionList

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
        val mainview = localInflater.inflate(R.layout.fragment_qurbani_details,container,false)

        _viewpager = mainview.findViewById(R.id._viewpager)
        actionbar = mainview.findViewById(R.id.actionbar)
        prevStep = mainview.findViewById(R.id.prevStep)
        NextStep = mainview.findViewById(R.id.NextStep)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = R.drawable.ic_settings,
            callback = this,
            actionnBartitle = localContext.getString(R.string.qurbani),
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

        prevStep.isEnabled = false

        prevStep.setOnClickListener {
            if(_viewpager.currentItem>0) {
                _viewpager.currentItem = _viewpager.currentItem - 1

            }
        }

        NextStep.setOnClickListener {
            if(_viewpager.currentItem>=0 && _viewpager.currentItem<mPageDestination.size-1) {
                _viewpager.currentItem = _viewpager.currentItem + 1
            }
        }

       initObserver()
        loadapi()

    }

    private fun loadapi(){
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getContentByCat(navArgs.data.SurahId,getLanguage())
        }
    }


    override fun selectedQurbaniBanner(getdata: Data) {
        val bundle = Bundle()
        bundle.putString("title", localContext.getString(R.string.qurbani))
        bundle.putString("imgUrl", "$BASE_CONTENT_URL_SGP${getdata.ImageUrl}")
        //bundle.putString("content","${getdata.Title}:\n\n${getdata.Text}\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")

        ImageViewPopupDialog.display(childFragmentManager, bundle)
    }

        private fun initObserver(){
        viewmodel.qurbaniLiveData.observe(viewLifecycleOwner){
            when(it){
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is QurbaniResource.QurbaniContentByCat ->{

                    data = it.data

                    it.data.forEachIndexed { index, qdata ->
                        val bundle = Bundle()
                        bundle.putParcelable("data",qdata)
                        bundle.putInt("total",it.data.size)
                        bundle.putInt("index",index)
                        mPageDestination.add(QurbaniContentFragment.newInstance(bundle))
                    }

                    mainViewPagerAdapter = MainViewPagerAdapter(
                        childFragmentManager,
                        lifecycle,
                        mPageDestination
                    )

                    _viewpager.apply {
                        adapter = mainViewPagerAdapter
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            isNestedScrollingEnabled = false
                        }
                        overScrollMode = View.OVER_SCROLL_NEVER
                        offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
                        reduceDragSensitivity(2)
                        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                prevStep.isEnabled = currentItem != 0
                                NextStep.isEnabled = currentItem != mPageDestination.size -1

                            }
                        })
                    }


                   /* banner.post {
                      val param = _viewpager.layoutParams as ViewGroup.MarginLayoutParams
                      param.topMargin = actionbar.height + banner.height + 12.dp
                      _viewpager.layoutParams = param
                    }*/

                    _viewpager.post {
                        baseViewState()
                    }



                }
            }
        }
    }

    override fun noInternetRetryClicked() {
        loadapi()
    }

    override fun action1() {

    }

    override fun action2() {

        contentSetting = AppPreference.getContentSetting()

        dialog?.dismiss()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_content_setting, null, false)

        val arabicFontSlider:Slider = customAlertDialogView.findViewById(R.id.arabicFontSlider)
        val arabicFontSize:AppCompatTextView = customAlertDialogView.findViewById(R.id.arabicFontSize)
        val banglaFontSlider:Slider = customAlertDialogView.findViewById(R.id.banglaFontSlider)
        val banglaFontSize:AppCompatTextView = customAlertDialogView.findViewById(R.id.banglaFontSize)
        val chooseArabicFont:MaterialButton = customAlertDialogView.findViewById(R.id.chooseArabicFont)


        chooseArabicFont.setOnClickListener {
            dialog_select_arabic_font()
        }

        arabicFontSlider.addOnChangeListener { _, value, _ ->
                contentSetting.arabicFontSize = value
            AppPreference.setContentSetting(contentSetting)
                lifecycleScope.launch(Dispatchers.IO) {
                    contentSettingViewModel.update(contentSetting)
                }
        }

        banglaFontSlider.addOnChangeListener { _, value, _ ->
            contentSetting.banglaFontSize = value
            AppPreference.setContentSetting(contentSetting)
            lifecycleScope.launch(Dispatchers.IO) {
                contentSettingViewModel.update(contentSetting)
            }
        }


        //apply setting
        val arbfontsize = (((contentSetting.arabicFontSize.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
        arabicFontSize.text = "${arbfontsize.toInt()}%".numberLocale()
        arabicFontSlider.value =  arbfontsize

        val bnfontsize = (((contentSetting.banglaFontSize.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
        banglaFontSize.text = "${bnfontsize.toInt()}%".numberLocale()
        banglaFontSlider.value =  bnfontsize

        chooseArabicFont.text = localContext.getArabicFontList().firstOrNull { trn-> trn.fontid == contentSetting.arabicFont.toString() }?.fontname.toString()


        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show().apply {
                window?.setGravity(Gravity.CENTER)
            }
    }

    override fun dialog_select_arabic_font()
    {

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = localInflater.inflate(R.layout.dialog_translator_list, null, false)

        // Initialize and assign variable

        val translationByTxt:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt)
        val banglaTranList: RecyclerView = customAlertDialogView.findViewById(R.id.banglaTranList)
        val translationByTxt1:AppCompatTextView = customAlertDialogView.findViewById(R.id.translationByTxt1)
        val englishTranList: RecyclerView = customAlertDialogView.findViewById(R.id.englishTranList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.pageTitle)

        translationByTxt1.hide()
        englishTranList.hide()

        translationByTxt.text = localContext.getString(R.string.font_settings)
        title.text = localContext.getString(R.string.arabic_font)

        banglaTranList.apply {
            arabicFontCommonSelectionList = PlayerCommonSelectionList(
                ArrayList(localContext.getArabicFontList().map { transformArabicFontData(it) }),this@QurbaniDetailsFragment)
            adapter = arabicFontCommonSelectionList

        }

        val updatedData = arabicFontCommonSelectionList.getData().map { data ->
            data.copy(isSelected = data.Id == contentSetting.arabicFont)
        }

        arabicFontCommonSelectionList.update(updatedData)

        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
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
        dialog?.dismiss()
    }

   /* override fun option3dotClicked(getdata: OptionList, ayathData: Ayath?) {

        when(getdata.type){


            "share" -> {
                val data = this.data?.get(_viewpager.currentItem)
                val title = data?.Title
                val content = data?.Text
                context?.shareText("${title}:\n\n$content\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")
            }

            "copy" -> {
                val data = this.data?.get(_viewpager.currentItem)
                val title = data?.Title
                val content = data?.Text
                context?.apply {
                    copyToClipboard("${title}:\n\n$content\n\nExplore a world of Islamic content on your fingertips. https://shorturl.at/GPSY6")
                    toast("Content copied")
                }

            }
        }

        dialog?.dismiss()

    }*/


}