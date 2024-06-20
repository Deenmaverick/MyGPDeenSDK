package com.deenislamic.sdk.views.quran.quranplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislamic.sdk.service.models.quran.quranplayer.ThemeResource
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Qari
import com.deenislamic.sdk.utils.transformPlayerReciterData
import com.deenislamic.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import com.deenislamic.sdk.views.adapters.quran.quranplayer.PlayerCommonSelectionList
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

internal class PlayerAudioFragment(private val qarisData: ArrayList<Qari> = arrayListOf()) : BaseRegularFragment(),
    PlayerCommonSelectionList.PlayerCommonSelectionListCallback {

    private val viewmodel by viewModels<PlayerControlViewModel>({requireActivity()})

    private lateinit var autoScrollSwitch:SwitchMaterial
    private lateinit var autoPlaySwitch:SwitchMaterial
    private lateinit var qariList: RecyclerView
    private lateinit var playerCommonSelectionList: PlayerCommonSelectionList

    private var updateSettingCall:Boolean = false

    private var selectedQariData:Qari ? = null

    // setting value
    private var auto_scroll = true
    private var auto_play_next = true
    private var selectedQari = 931

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_player_audio, container, false)

        //init view
        autoScrollSwitch = mainview.findViewById(R.id.autoScrollSwitch)
        autoPlaySwitch = mainview.findViewById(R.id.autoPlaySwitch)
        qariList = mainview.findViewById(R.id.qariList)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        autoScrollSwitch.setOnCheckedChangeListener { compoundButton, b ->

            lifecycleScope.launch {
                auto_scroll = b
                updateSettingCall = true
                viewmodel.updateAudioSetting(auto_scroll,auto_play_next,selectedQari)
            }

        }

        autoPlaySwitch.setOnCheckedChangeListener { compoundButton, b ->

            lifecycleScope.launch {
                auto_play_next = b
                updateSettingCall = true
                viewmodel.updateAudioSetting(auto_scroll,auto_play_next,selectedQari)
            }

        }

        qariList.apply {
            playerCommonSelectionList = PlayerCommonSelectionList(ArrayList(qarisData.map { transformPlayerReciterData(it) }),this@PlayerAudioFragment)
            adapter = playerCommonSelectionList
        }

        initObserver()
        loadSetting()


    }

    private fun loadSetting()
    {
        lifecycleScope.launch {
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
                    it.setting?.auto_scroll?.let { it1 ->
                        if(!updateSettingCall)
                            autoScrollSwitch.isChecked = it1
                    }

                    it.setting?.auto_play_next?.let { it1 ->
                        if(!updateSettingCall)
                            autoPlaySwitch.isChecked = it1
                    }

                    it.setting?.recitation?.let {
                            it1->

                        playerCommonSelectionList.update(updateSelectedQari(it1))
                    }
                }

                is ThemeResource.updatePlayerSettings ->
                {
                    it.setting?.auto_scroll?.let { it1 ->
                        if(!updateSettingCall)
                            autoScrollSwitch.isChecked = it1
                    }

                    it.setting?.auto_play_next?.let { it1 ->
                        if(!updateSettingCall)
                            autoPlaySwitch.isChecked = it1
                    }

                    it.setting?.recitation?.let {
                            it1->
                        playerCommonSelectionList.update(updateSelectedQari(it1))
                    }
                }
            }
        }
    }



    private fun updateSelectedQari(selectedQariID: Int): List<PlayerCommonSelectionData> {

        var finalQari = selectedQari

        if(selectedQariID != 1)
            finalQari = selectedQariID

        val crrentData = playerCommonSelectionList.getData()

        val updatedData = crrentData.map { qari ->
            qari.copy(isSelected = qari.Id == finalQari)
        }

        return updatedData
    }

    override fun playerCommonListSelected(
        data: PlayerCommonSelectionData,
        adapter: PlayerCommonSelectionList
    ) {
        lifecycleScope.launch {
            viewmodel.updateAudioSetting(auto_scroll,auto_play_next,data.Id)
        }
    }


}