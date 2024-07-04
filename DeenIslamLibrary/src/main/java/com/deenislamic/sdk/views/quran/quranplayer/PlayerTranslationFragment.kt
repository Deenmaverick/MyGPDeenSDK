package com.deenislamic.sdk.views.quran.quranplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.models.quran.AlQuranSettingResource
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Translator
import com.deenislamic.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import com.deenislamic.sdk.utils.AlQuranSetting_bn_translator
import com.deenislamic.sdk.utils.AlQuranSetting_en_translator
import com.deenislamic.sdk.viewmodels.common.PlayerControlVMFactory
import com.deenislamic.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.launch


internal class PlayerTranslationFragment(private val translatorDataData: ArrayList<Translator>) : BaseRegularFragment(),PlayerCommonSelectionList.PlayerCommonSelectionListCallback {

    private lateinit var banglaTranList:RecyclerView
    private lateinit var englishTranList:RecyclerView
    private lateinit var enPlayerCommonSelectionList: PlayerCommonSelectionList
    private lateinit var bnPlayerCommonSelectionList: PlayerCommonSelectionList


    private lateinit var viewmodel:PlayerControlViewModel

    //private lateinit var fontControl: Slider
    //private lateinit var defaultFontBtn: AppCompatTextView
    private lateinit var transliterationSwitch:MaterialSwitch

    private var updateSettingCall:Boolean = false
    //setting
    private var translation_font_size:Float = 0F
    private var transliteration:Boolean = true
    private var en_translator = 131
    private var bn_translator = 161

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
        val mainview = localInflater.inflate(R.layout.fragment_player_translation, container, false)

        //init view
        //fontControl = mainview.findViewById(R.id.fontControl)
        //defaultFontBtn = mainview.findViewById(R.id.defaultFontBtn)
        transliterationSwitch = mainview.findViewById(R.id.autoScrollSwitch)
        banglaTranList = mainview.findViewById(R.id.banglaTranList)
        englishTranList = mainview.findViewById(R.id.englishTranList)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        /*fontControl.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            //setupFontControl(value)
            lifecycleScope.launch {
                translation_font_size = value
                updateSettingCall = true
                viewmodel.updateTranslationSetting(translation_font_size,transliteration)
            }

        }*/

        // transliteration switch

        transliterationSwitch.setOnCheckedChangeListener { compoundButton, b ->

            lifecycleScope.launch {
                transliteration = b
                viewmodel.updateTranslationSetting(translation_font_size,transliteration,en_translator,bn_translator)
            }

        }

        banglaTranList.apply {
            bnPlayerCommonSelectionList = PlayerCommonSelectionList(
                ArrayList(translatorDataData.map { transformdata(it) }
                    .filter { it.language == "bn" }
                ),this@PlayerTranslationFragment)
            adapter = bnPlayerCommonSelectionList
        }

        englishTranList.apply {
            enPlayerCommonSelectionList = PlayerCommonSelectionList(
                ArrayList(translatorDataData.map { transformdata(it) }
                    .filter { it.language == "en" }
                ),this@PlayerTranslationFragment)
            adapter = enPlayerCommonSelectionList
        }

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
        viewmodel.alQuranSettingsLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is AlQuranSettingResource.AlQuranSettings ->
                {
                    //it.setting?.translation_font_size?.let { it1 -> translation_font_size = it1 }

                    it.setting?.en_translator?.let {
                        it1-> enPlayerCommonSelectionList.update(updateEnTranslator(it1))
                    }

                    it.setting?.bn_translator?.let {
                            it1-> bnPlayerCommonSelectionList.update(updateBnTranslator(it1))
                    }
                }

                is AlQuranSettingResource.UpdateAlQuranSettings ->
                {
                    //it.setting?.translation_font_size?.let { it1 -> setupFontControl(it1) }

                    if(it.type == AlQuranSetting_en_translator){
                        it.setting?.en_translator?.let {
                                it1-> enPlayerCommonSelectionList.update(updateEnTranslator(it1))
                        }
                    }

                    if(it.type == AlQuranSetting_bn_translator){
                        it.setting?.bn_translator?.let {
                                it1-> bnPlayerCommonSelectionList.update(updateBnTranslator(it1))
                        }
                    }

                }
            }
        }
    }


    private fun updateBnTranslator(selectedTranslator: Int): List<PlayerCommonSelectionData> {

        var finalTransalator = bn_translator

        if(selectedTranslator != 0)
            finalTransalator = selectedTranslator

        val crrentData = bnPlayerCommonSelectionList.getData()

        val updatedData = crrentData.map { data ->
            data.copy(isSelected = data.Id == finalTransalator)
        }

        return updatedData
    }

    private fun updateEnTranslator(selectedTranslator: Int): List<PlayerCommonSelectionData> {

        var finalTransalator = en_translator

        if(selectedTranslator != 0)
            finalTransalator = selectedTranslator

        val crrentData = enPlayerCommonSelectionList.getData()

        val updatedData = crrentData.map { data ->
            data.copy(isSelected = data.Id == finalTransalator)
        }

        return updatedData
    }


    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {

        if(adapter == enPlayerCommonSelectionList) {
            en_translator = data.Id
            lifecycleScope.launch {
                viewmodel.updateTranslator(data.Id, AlQuranSetting_en_translator)
            }
        }
        else if(adapter == bnPlayerCommonSelectionList)
        {
            bn_translator = data.Id
            lifecycleScope.launch {
                viewmodel.updateTranslator(data.Id, AlQuranSetting_bn_translator)
            }
        }
    }

    private fun transformdata(newDataModel: Translator): PlayerCommonSelectionData {

        return PlayerCommonSelectionData(
            imageurl = null,
            Id = newDataModel.title,
            title = newDataModel.text,
            language = newDataModel.language
        )
    }

}