package com.deenislamic.sdk.views.quran.quranplayer

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.entity.PlayerSettingPref
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.models.quran.AlQuranSettingResource
import com.deenislamic.sdk.service.models.quran.quranplayer.FontList
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import com.deenislamic.sdk.utils.AlQuranSetting_all
import com.deenislamic.sdk.utils.AlQuranSetting_arabic_font
import com.deenislamic.sdk.utils.AlQuranSetting_bn_meaning
import com.deenislamic.sdk.utils.AlQuranSetting_bn_pronounce
import com.deenislamic.sdk.utils.DEFAULT_ARABIC_FONT_ID
import com.deenislamic.sdk.utils.drawer
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.player
import com.deenislamic.sdk.viewmodels.common.PlayerControlVMFactory
import com.deenislamic.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


internal class PlayerThemeFragment : BaseRegularFragment(),
    PlayerCommonSelectionList.PlayerCommonSelectionListCallback {

    private lateinit var viewmodel: PlayerControlViewModel

    private lateinit var arabicFontControl:ConstraintLayout
    private lateinit var fontControl:Slider
    private lateinit var ayatArabic:AppCompatTextView
    private lateinit var defaultFontBtn:AppCompatTextView
    private lateinit var arabicFontsizeTxt:AppCompatTextView

    private lateinit var banglaFontControlLy:ConstraintLayout
    private lateinit var banglaFontControl:Slider
    private lateinit var banglaFontTxt:AppCompatTextView
    private lateinit var banglaDefaultFontBtn:AppCompatTextView
    private lateinit var banglaFontsizeTxt:AppCompatTextView

    private lateinit var englishFontControlLy:ConstraintLayout
    private lateinit var englishFontControl:Slider
    private lateinit var englishFontTxt:AppCompatTextView
    private lateinit var englishDefaultFontBtn:AppCompatTextView
    private lateinit var englishFontsizeTxt:AppCompatTextView

    private val fontListData:ArrayList<FontList> = arrayListOf()

    private lateinit var fontList:RecyclerView
    private lateinit var fontListAdapter:PlayerCommonSelectionList


    private var updateSettingCall:Boolean = false

    private var isArabicFontSizeCall = false
    private var isBanglaFontSizeCall = false
    private var isEnglishFontSizeCall = false

    private lateinit var bnPronunceSwitch:MaterialSwitch
    private lateinit var bnMeaningSwitch:MaterialSwitch

    override fun OnCreate() {
        super.OnCreate()

        val repository = PlayerControlRepository(
            playerSettingDao = DatabaseProvider().getInstance().providePlayerSettingDao()
        )

        val factory = PlayerControlVMFactory(repository)
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[PlayerControlViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_player_theme, container, false)

        //init view
        arabicFontControl = mainview.findViewById(R.id.arabicFontControl)
        fontControl = arabicFontControl.findViewById(R.id.fontControl)
        ayatArabic = arabicFontControl.findViewById(R.id.ayatArabic)
        defaultFontBtn = arabicFontControl.findViewById(R.id.defaultFontBtn)
        arabicFontsizeTxt = arabicFontControl.findViewById(R.id.fontsizeTxt)


        banglaFontControlLy = mainview.findViewById(R.id.banglaFontControlLy)
        banglaFontControl = banglaFontControlLy.findViewById(R.id.fontControl)
        banglaFontTxt = banglaFontControlLy.findViewById(R.id.ayatArabic)
        banglaDefaultFontBtn = banglaFontControlLy.findViewById(R.id.defaultFontBtn)
        banglaFontsizeTxt = banglaFontControlLy.findViewById(R.id.fontsizeTxt)

        englishFontControlLy = mainview.findViewById(R.id.englishFontControlLy)
        englishFontControl = englishFontControlLy.findViewById(R.id.fontControl)
        englishFontTxt = englishFontControlLy.findViewById(R.id.ayatArabic)
        englishDefaultFontBtn = englishFontControlLy.findViewById(R.id.defaultFontBtn)
        englishFontsizeTxt = englishFontControlLy.findViewById(R.id.fontsizeTxt)

        bnPronunceSwitch = mainview.findViewById(R.id.bnPronunceSwitch)
        bnMeaningSwitch = mainview.findViewById(R.id.bnMeaningSwitch)

        fontList = mainview.findViewById(R.id.fontList)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arabicFontsizeTxt.text = localContext.getString(R.string.arabic_font_size)
        banglaFontsizeTxt.text = localContext.getString(R.string.bangla_font_size)
        englishFontsizeTxt.text = localContext.getString(R.string.english_font_size)
        banglaFontTxt.text = "বিসমিল্লাহির রাহমানির রাহিম"
        englishFontTxt.text = "bismillah ar-rahman ar-rahim"


        fontListData.add(FontList(fontname = localContext.getString(R.string.indopak), fontid = "1"))
        fontListData.add(FontList(fontname = localContext.getString(R.string.uthmanic_script_hafs_regular), fontid = "2"))
        fontListData.add(FontList(fontname = localContext.getString(R.string.al_majeed), fontid = "3"))

        fontControl.addOnChangeListener { slider, value, fromUser ->

            if(!isArabicFontSizeCall) {
                defaultFontBtn.text = "${value.toInt()}%".numberLocale()
                setupFontControl(value, isThemeFont = true)
                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.updateFontSize(value, "ptc_arabic")
                }
            }else
                isArabicFontSizeCall = false

        }

        banglaFontControl.addOnChangeListener { slider, value, fromUser ->

            if(!isBanglaFontSizeCall) {
                banglaDefaultFontBtn.text = "${value.toInt()}%".numberLocale()
                setupFontControl(value, isTranslationFont = true)
                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.updateFontSize(value, "ptc_bangla")
                }
            }else
                isBanglaFontSizeCall = false

        }

        englishFontControl.addOnChangeListener { slider, value, fromUser ->

            if(!isEnglishFontSizeCall) {
                englishDefaultFontBtn.text = "${value.toInt()}%".numberLocale()
                setupFontControl(value, isEnglishFont = true)
                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.updateFontSize(value, "ptc_english")
                }
            }else
                isEnglishFontSizeCall = false

        }

        fontList.apply {
            fontListAdapter = PlayerCommonSelectionList(
                ArrayList(fontListData.map { it1-> transformdata(it1) }),this@PlayerThemeFragment)
            adapter = fontListAdapter
        }

        //fontListAdapter.update(updateSelectedFont(arabic_font))

       /* uthmaniFontLayout.setOnClickListener {
            lifecycleScope.launch {
                arabic_font = 2
                viewmodel.updateThemeSetting(theme_font_size,arabic_font)
            }
        }

        indopakFontLayout.setOnClickListener {
            lifecycleScope.launch {
                arabic_font = 1
                viewmodel.updateThemeSetting(theme_font_size,arabic_font)
            }
        }*/

        bnPronunceSwitch.setOnCheckedChangeListener { _, b ->
            lifecycleScope.launch {
                viewmodel.updateBanglaPronounce(b,player+ AlQuranSetting_bn_pronounce)
            }
        }

        bnMeaningSwitch.setOnCheckedChangeListener { _, b ->
            lifecycleScope.launch {
                viewmodel.updateBanglaMeaning(b,player+AlQuranSetting_bn_meaning)
            }
        }

        initObserver()
        loadSetting()
    }

    private fun loadSetting()
    {
        lifecycleScope.launch {
            updateSettingCall = true
            viewmodel.getSetting()
        }
    }

    private fun initObserver()
    {
        viewmodel.alQuranSettingsLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranSettingResource.AlQuranSettings ->
                {
                    //it.setting?.theme_font_size?.let { it1 -> setupFontControl(it1) }
                    //it.setting?.translation_font_size?.let { it1 -> setupBanglaFontControl(it1) }

                    updateThemeSetting(it.setting,AlQuranSetting_all)


                    it.setting?.arabic_font?.let { it1->

                        fontListAdapter.update(updateSelectedFont(it1))

                        when(it1){

                            1-> {
                                val customFont = ResourcesCompat.getFont(requireContext(), R.font.indopak)
                                ayatArabic.typeface = customFont
                                ayatArabic.text = getString(R.string.demo_indopakTxt)
                            }

                            2-> {
                                val customFont = ResourcesCompat.getFont(requireContext(), R.font.kfgqpc_font)
                                ayatArabic.typeface = customFont
                                ayatArabic.text = getString(R.string.demoUthamaniTxt)
                            }

                            3-> {
                                val customFont = ResourcesCompat.getFont(requireContext(), R.font.al_majed_quranic_font_regular)
                                ayatArabic.typeface = customFont
                                ayatArabic.text = getString(R.string.demo_almajeed)
                            }
                        }
                    }

                }

                is AlQuranSettingResource.UpdateAlQuranSettings ->
                {

                    updateThemeSetting(it.setting,it.type)

                    //it.setting?.theme_font_size?.let { it1 -> setupFontControl(it1) }
                    //it.setting?.translation_font_size?.let { it1 -> setupBanglaFontControl(it1) }

                    if(it.type == AlQuranSetting_arabic_font || it.type == AlQuranSetting_all) {
                        it.setting?.arabic_font?.let { it1 ->

                            fontListAdapter.update(updateSelectedFont(it1))

                            when (it1) {

                                1 -> {
                                    val customFont =
                                        ResourcesCompat.getFont(requireContext(), R.font.indopak)
                                    ayatArabic.typeface = customFont
                                    ayatArabic.text = getString(R.string.demo_indopakTxt)
                                }

                                2 -> {
                                    val customFont = ResourcesCompat.getFont(
                                        requireContext(),
                                        R.font.kfgqpc_font
                                    )
                                    ayatArabic.typeface = customFont
                                    ayatArabic.text = getString(R.string.demoUthamaniTxt)
                                }

                                3 -> {
                                    val customFont = ResourcesCompat.getFont(
                                        requireContext(),
                                        R.font.al_majed_quranic_font_regular
                                    )
                                    ayatArabic.typeface = customFont
                                    ayatArabic.text = getString(R.string.demo_almajeed)
                                }
                            }
                        }
                    }
                   /* clearActiveFont()
                    if(it.setting?.arabic_font == 1)
                    {
                        val customFont = ResourcesCompat.getFont(requireContext(), R.font.indopak)
                        ayatArabic.typeface = customFont
                        ayatArabic.text = getString(R.string.demo_indopakTxt)
                        setActiveFont(indopakTxt, indopak)
                    }
                    else {

                        val customFont = ResourcesCompat.getFont(requireContext(), R.font.kfgqpc_font)
                        ayatArabic.typeface = customFont
                        ayatArabic.text = getString(R.string.demoUthamaniTxt)
                        setActiveFont(uthmaniTxt,uthmani)
                    }*/
                }
            }
        }
    }


    private fun setupFontControl(
        fontsize: Float,
        isTranslationFont:Boolean=false,
        isThemeFont:Boolean=false,
        isEnglishFont:Boolean=false
    )
    {

        when(fontsize)
        {
            0F ->
            {
                if(isThemeFont) {
                    ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,25F)
                    defaultFontBtn.text = localContext.getString(R.string.default_txt)
                }

                if(isTranslationFont) {
                    banglaFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,14F)
                    banglaDefaultFontBtn.text = localContext.getString(R.string.default_txt)
                }

                if(isEnglishFont) {
                    englishFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,14F)
                    englishDefaultFontBtn.text = localContext.getString(R.string.default_txt)
                }

            }

            20F ->
            {
                if(isThemeFont) {
                    ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,29F)
                    defaultFontBtn.text = "20%".numberLocale()
                }

                if(isTranslationFont) {
                    banglaFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,17F)
                    banglaDefaultFontBtn.text = "20%".numberLocale()
                }

                if(isEnglishFont) {
                    englishFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,17F)
                    englishDefaultFontBtn.text = "20%".numberLocale()
                }

            }

            40F ->
            {
                if(isThemeFont) {
                    ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,31F)
                    defaultFontBtn.text = "40%".numberLocale()
                }

                if(isTranslationFont) {
                    banglaFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,19F)
                    banglaDefaultFontBtn.text = "40%".numberLocale()
                }

                if(isEnglishFont) {
                    englishFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,19F)
                    englishDefaultFontBtn.text = "40%".numberLocale()
                }

            }

            60F ->
            {
                if(isThemeFont) {
                    ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,34F)
                    defaultFontBtn.text = "60%".numberLocale()
                }

                if(isTranslationFont) {
                    banglaFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,22F)
                    banglaDefaultFontBtn.text = "60%".numberLocale()
                }

                if(isEnglishFont) {
                    englishFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,22F)
                    englishDefaultFontBtn.text = "60%".numberLocale()
                }

            }

            80F->
            {

                if(isThemeFont) {
                    ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,37F)
                    defaultFontBtn.text = "80%".numberLocale()
                }

                if(isTranslationFont) {
                    banglaFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,25F)
                    banglaDefaultFontBtn.text = "80%".numberLocale()
                }

                if(isEnglishFont) {
                    englishFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,25F)
                    englishDefaultFontBtn.text = "80%".numberLocale()
                }


            }

            100F ->
            {

                if(isThemeFont) {
                    ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,42F)
                    defaultFontBtn.text = "100%".numberLocale()
                }

                if(isTranslationFont) {
                    banglaFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,29F)
                    banglaDefaultFontBtn.text = "100%".numberLocale()
                }

                if(isEnglishFont) {
                    englishFontTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,29F)
                    englishDefaultFontBtn.text = "100%".numberLocale()
                }

            }
        }

    }


    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {

        lifecycleScope.launch {
            viewmodel.updateArabicFont(data.Id)
        }
    }

    private fun transformdata(newDataModel: FontList): PlayerCommonSelectionData {

        return PlayerCommonSelectionData(
            imageurl = null,
            Id = newDataModel.fontid.toInt(),
            title = newDataModel.fontname
        )
    }

    private fun updateSelectedFont(fontid: Int): List<PlayerCommonSelectionData> {

        var finalTransalator = DEFAULT_ARABIC_FONT_ID

        if(fontid != 0)
            finalTransalator = fontid

        val crrentData = fontListAdapter.getData()

        val updatedData = crrentData.map { data ->
            data.copy(isSelected = data.Id == finalTransalator)
        }

        return updatedData
    }

    private fun updateThemeSetting(setting: PlayerSettingPref?, type:String) {

        if(type == "arabic" || type == AlQuranSetting_all) {
            setting?.theme_font_size?.let { it1 ->
                val fontsize =
                    (((it1.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
                defaultFontBtn.text = "${fontsize.toInt()}%".numberLocale()
                if (fontControl.value != it1)
                    isArabicFontSizeCall = true
                fontControl.value = fontsize
                setupFontControl(it1, isThemeFont = true)
            }
        }

        if(type == "bangla" || type == AlQuranSetting_all) {
            setting?.translation_font_size?.let { it1 ->
                val fontsize =
                    (((it1.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
                banglaDefaultFontBtn.text = "${fontsize.toInt()}%".numberLocale()
                if (banglaFontControl.value != it1)
                    isBanglaFontSizeCall = true
                banglaFontControl.value = fontsize
                setupFontControl(it1, isTranslationFont = true)
            }
        }

        if(type == "english" || type == AlQuranSetting_all) {
            setting?.english_font_size?.let { it1 ->
                val fontsize = (((it1.coerceIn(0F, 100F) + 10) / 20).toInt() * 20).toFloat()
                englishDefaultFontBtn.text = "${fontsize.toInt()}%".numberLocale()
                if(englishFontControl.value !=it1)
                    isEnglishFontSizeCall = true
                englishFontControl.value =  fontsize
                setupFontControl(it1, isEnglishFont = true)
            }
        }

        if(type == drawer + AlQuranSetting_bn_pronounce || type == AlQuranSetting_all){
            setting?.transliteration?.let {
                    it1 -> bnPronunceSwitch.isChecked = it1
            }
        }

        if(type == drawer+ AlQuranSetting_bn_meaning || type == AlQuranSetting_all){
            setting?.bn_meaning?.let {
                    it1 -> bnMeaningSwitch.isChecked = it1
            }
        }

    }

    }