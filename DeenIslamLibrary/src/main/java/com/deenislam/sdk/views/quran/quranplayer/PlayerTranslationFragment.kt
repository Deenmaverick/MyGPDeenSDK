package com.deenislam.sdk.views.quran.quranplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.models.quran.quranplayer.ThemeResource
import com.deenislam.sdk.service.repository.quran.quranplayer.PlayerControlRepository
import com.deenislam.sdk.viewmodels.quran.quranplayer.PlayerControlViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

internal class PlayerTranslationFragment : BaseRegularFragment() {

    private lateinit var viewmodel: PlayerControlViewModel

    private lateinit var fontControl: Slider
    private lateinit var defaultFontBtn: AppCompatTextView
    private lateinit var transliterationSwitch:MaterialSwitch

    private var updateSettingCall:Boolean = false
    //setting
    private var translation_font_size:Float = 0F
    private var transliteration:Boolean = true

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
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_player_translation, container, false)

        //init view
        fontControl = mainview.findViewById(R.id.fontControl)
        defaultFontBtn = mainview.findViewById(R.id.defaultFontBtn)
        transliterationSwitch = mainview.findViewById(R.id.autoScrollSwitch)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSetting()
        initObserver()

        fontControl.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            setupFontControl(value)
            lifecycleScope.launch {
                translation_font_size = value
                updateSettingCall = true
                viewmodel.updateTranslationSetting(translation_font_size,transliteration)
            }

        }

        // transliteration switch

        transliterationSwitch.setOnCheckedChangeListener { compoundButton, b ->

            lifecycleScope.launch {
                transliteration = b
                viewmodel.updateTranslationSetting(translation_font_size,transliteration)
            }

        }
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
                    it.setting?.translation_font_size?.let { it1 -> setupFontControl(it1) }
                }
            }
        }
    }

    private fun setupFontControl(value:Float)
    {

        when(value)
        {
            0F ->
            {
                defaultFontBtn.text = "Default"
            }

            20F ->
            {
                defaultFontBtn.text = "120%"
            }

            40F ->
            {
                defaultFontBtn.text = "140%"
            }

            60F ->
            {
                defaultFontBtn.text = "160%"
            }

            80F->
            {
                defaultFontBtn.text = "180%"
            }

            100F ->
            {
                defaultFontBtn.text = "200%"
            }

            else -> updateSettingCall = true
        }

        if(!updateSettingCall)
            fontControl.value = value
    }

    inner class VMFactory(
        private val repository: PlayerControlRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayerControlViewModel(repository) as T
        }
    }

}