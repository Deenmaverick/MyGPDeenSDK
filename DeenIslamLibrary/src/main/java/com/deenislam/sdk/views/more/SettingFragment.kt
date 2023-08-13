package com.deenislam.sdk.views.more

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.models.SettingResource
import com.deenislam.sdk.service.repository.SettingRepository
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.viewmodels.SettingViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SettingFragment : BaseRegularFragment(), otherFagmentActionCallback {

    private lateinit var languageLayout:MaterialCardView
    private var dialog: Dialog? = null
    private var bnLayout:LinearLayout ? = null
    private var enLayout:LinearLayout ? = null
    private var bnTitle:AppCompatTextView ? = null
    private var enTitle:AppCompatTextView ? = null
    private var banglaRadioBtn:RadioButton ? = null
    private var englishRadioBtn:RadioButton ? = null
    private lateinit var locationswitch: MaterialSwitch
    private lateinit var currentLanguage:AppCompatTextView

    private var localLanguage = "bn"

    private lateinit var viewmodel:SettingViewModel

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

        // init viewmodel
        val repository = SettingRepository(userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao())
        viewmodel = SettingViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainview = localInflater.inflate(R.layout.fragment_setting,container,false)

        //init view
        languageLayout = mainview.findViewById(R.id.languageLayout)
        locationswitch = mainview.findViewById(R.id.locationswitch)
        currentLanguage = mainview.findViewById(R.id.subTitle1)
        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = this@SettingFragment,
            actionnBartitle = localContext.getString(R.string.setting),
            backEnable = true,
            view = mainview
        )

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()

        lifecycleScope.launch {
            viewmodel.getSetting()
        }

        languageLayout.setOnClickListener {
           // showLanguiageDialog()
        }

        locationswitch.setOnClickListener {

           lifecycleScope.launch {
               viewmodel.updateLocationSetting(!locationswitch.isChecked)
           }
        }

    }

    private fun initObserver()
    {
        viewmodel.settingLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is SettingResource.languageFailed -> requireContext().toast("Something went wrong")
                is SettingResource.settingData -> {

                    localLanguage = if(it.data?.language == "en")
                        "en"
                    else
                        "bn"


                    setupSettingDialogLanguage()

                    locationswitch.isChecked = it.data?.location_setting == false

                }

                is SettingResource.languageDataUpdate ->
                {
                    localLanguage = if(it.language.toString() == "en")
                        "en"
                    else
                        "bn"

                    setupSettingDialogLanguage()
                    lifecycleScope.launch {
                        viewmodel.clear()
                        withContext(Dispatchers.Main)
                        {
                            changeLanguage(localLanguage)
                            dialog?.dismiss()
                            val navController = findNavController()
                            navController.popBackStack()
                            navController.navigate(R.id.action_moreFragment_to_settingFragment)
                        }
                    }
                }
            }
        }
    }


    override fun action1() {
    }

    override fun action2() {
    }

    private fun showLanguiageDialog()
    {
        dialog = BottomSheetDialog(requireContext())

        val view = localInflater.inflate(R.layout.dialog_language, null)
        bnLayout = view.findViewById(R.id.bnLayout)
        bnTitle = view.findViewById(R.id.bnTitle)
        banglaRadioBtn = view.findViewById(R.id.banglaRadioBtn)
        enLayout = view.findViewById(R.id.enLayout)
        enTitle = view.findViewById(R.id.enTitle)
        englishRadioBtn = view.findViewById(R.id.englishRadioBtn)

        setupSettingDialogLanguage()

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()

        bnLayout?.setOnClickListener {
            lifecycleScope.launch {
                viewmodel.updateSetting("bn")
            }
        }

        enLayout?.setOnClickListener {
            lifecycleScope.launch {
                viewmodel.updateSetting("en")
            }
        }

    }

    private fun setupSettingDialogLanguage()
    {

        Deen.language = localLanguage

         if(localLanguage == "en")
        {
            enTitle?.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_primary))
            englishRadioBtn?.isChecked = true

            banglaRadioBtn?.isChecked = false
            bnTitle?.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_black_deep))


        }
        else
        {
            enTitle?.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_txt_black_deep))
            englishRadioBtn?.isChecked = false

            banglaRadioBtn?.isChecked = true
            bnTitle?.setTextColor(ContextCompat.getColor(requireContext(),R.color.deen_primary))


        }

    }

}