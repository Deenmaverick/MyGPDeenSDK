package com.deenislamic.sdk.views.quran.quranplayer

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.quran.quranplayer.FontList
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.models.quran.quranplayer.ThemeResource
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class PlayerThemeFragment : BaseRegularFragment(),
    PlayerCommonSelectionList.PlayerCommonSelectionListCallback {

    private val viewmodel by viewModels<PlayerControlViewModel>({requireActivity()})

    private lateinit var fontControl:Slider
    private lateinit var ayatArabic:AppCompatTextView
    private lateinit var defaultFontBtn:AppCompatTextView

    private val fontListData:ArrayList<FontList> = arrayListOf()

    private lateinit var fontList: RecyclerView
    private lateinit var fontListAdapter:PlayerCommonSelectionList


    private var updateSettingCall:Boolean = false
    //setting
    private var theme_font_size:Float = 0F
    private var arabic_font:Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_player_theme, container, false)

        //init view
        fontControl = mainview.findViewById(R.id.fontControl)
        ayatArabic = mainview.findViewById(R.id.ayatArabic)
        defaultFontBtn = mainview.findViewById(R.id.defaultFontBtn)
        fontList = mainview.findViewById(R.id.fontList)


        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fontListData.add(FontList(fontname = localContext.getString(R.string.indopak), fontid = "1"))
        fontListData.add(FontList(fontname = localContext.getString(R.string.uthmanic_script_hafs_regular), fontid = "2"))
        fontListData.add(FontList(fontname = localContext.getString(R.string.al_majeed), fontid = "3"))

        fontControl.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            Log.e("fontControl",value.toString())
            //setupFontControl(value)
            lifecycleScope.launch(Dispatchers.IO) {
                theme_font_size = value
                //updateSettingCall = true
                viewmodel.updateThemeSetting(theme_font_size,arabic_font)
            }

        }

        fontList.apply {
            fontListAdapter = PlayerCommonSelectionList(
                ArrayList(fontListData.map { it1-> transformdata(it1) }),this@PlayerThemeFragment)
            adapter = fontListAdapter
        }

        fontListAdapter.update(updateSelectedFont(arabic_font))

        initObserver()
        loadSetting()
    }

    private fun loadSetting()
    {
        lifecycleScope.launch {
            updateSettingCall = false
            viewmodel.getSetting()
        }
    }

    private fun initObserver()
    {
        viewmodel.themeLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is ThemeResource.playerSettings ->
                {
                    updateSettingCall = true
                    it.setting?.theme_font_size?.let { it1 -> setupFontControl(it1) }

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

                is ThemeResource.updatePlayerSettings ->
                {
                    updateSettingCall = true
                    it.setting?.theme_font_size?.let { it1 -> setupFontControl(it1) }

                    it.setting?.arabic_font?.let { it1 ->

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
            }
        }
    }

    private fun setupFontControl(value:Float)
    {
        val minValue = 0F
        val maxValue = 100F

        when(value.coerceIn(minValue, maxValue))
        {
            0F ->
            {
                ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,24F)
                defaultFontBtn.text = localContext.getString(R.string.default_txt)

            }

            20F ->
            {
                ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,26F)
                defaultFontBtn.text = "20%".numberLocale()
            }

            40F ->
            {
                ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,28F)
                defaultFontBtn.text = "40%".numberLocale()
            }

            60F ->
            {
                ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,30F)
                defaultFontBtn.text = "60%".numberLocale()
            }

            80F->
            {
                ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
                defaultFontBtn.text = "80%".numberLocale()
            }

            100F ->
            {
                ayatArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,34F)
                defaultFontBtn.text = "100%".numberLocale()
            }
            else -> updateSettingCall = true
        }

        if(fontControl.value!=value) {

            fontControl.value =  value.coerceIn(minValue, maxValue)
        }
    }


    private fun setActiveFont(fontTxt:AppCompatTextView,font: AppCompatRadioButton)
    {
        fontTxt.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_primary))
        font.isChecked = true
    }

    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {
        Log.e("playerCommonListSele",data.toString())
        arabic_font = data.Id
        lifecycleScope.launch {
            viewmodel.updateThemeSetting(theme_font_size,arabic_font)
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

        var finalTransalator = arabic_font

        if(fontid != 0)
            finalTransalator = fontid

        val crrentData = fontListAdapter.getData()

        val updatedData = crrentData.map { data ->
            data.copy(isSelected = data.Id == finalTransalator)
        }

        return updatedData
    }

}