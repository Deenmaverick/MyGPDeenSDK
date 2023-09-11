package com.deenislam.sdk.views.quran.quranplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.models.quran.quranplayer.ThemeResource
import com.deenislam.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import com.deenislam.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

internal class PlayerAudioFragment : BaseRegularFragment() {

    private lateinit var viewmodel:PlayerControlViewModel

    private lateinit var autoScrollSwitch: SwitchMaterial
    private lateinit var autoPlaySwitch:SwitchMaterial

    private var updateSettingCall:Boolean = false

    // setting value
    private var auto_scroll = true
    private var auto_play_next = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init viewmodel
        val repository = PlayerControlRepository(DatabaseProvider().getInstance().providePlayerSettingDao())

        val factory = VMFactory(repository)
        viewmodel = ViewModelProvider(
            requireActivity(),
            factory
        )[PlayerControlViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_player_audio, container, false)

        //init view
        autoScrollSwitch = mainview.findViewById(R.id.autoScrollSwitch)
        autoPlaySwitch = mainview.findViewById(R.id.autoPlaySwitch)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSetting()
        initObserver()

        autoScrollSwitch.setOnCheckedChangeListener { compoundButton, b ->

            lifecycleScope.launch {
                auto_scroll = b
                updateSettingCall = true
                viewmodel.updateAudioSetting(auto_scroll,auto_play_next)
            }

        }

        autoPlaySwitch.setOnCheckedChangeListener { compoundButton, b ->

            lifecycleScope.launch {
                auto_play_next = b
                updateSettingCall = true
                viewmodel.updateAudioSetting(auto_scroll,auto_play_next)
            }

        }


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
                }
            }
        }
    }

    inner class VMFactory(
        private val repository: PlayerControlRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayerControlViewModel(repository) as T
        }
    }

}