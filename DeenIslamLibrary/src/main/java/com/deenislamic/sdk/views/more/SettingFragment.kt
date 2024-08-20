package com.deenislamic.sdk.views.more

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
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.DatabaseProvider
import com.deenislamic.sdk.service.models.SettingResource
import com.deenislamic.sdk.service.repository.SettingRepository
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.SettingViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.deenislamic.sdk.views.dashboard.DashboardFakeFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

internal class SettingFragment : BaseRegularFragment(), otherFagmentActionCallback {

    private lateinit var languageLayout:MaterialCardView
    private var dialog: Dialog? = null
    private var bnLayout:LinearLayout ? = null
    private var enLayout:LinearLayout ? = null
    private var bnTitle:AppCompatTextView ? = null
    private var enTitle:AppCompatTextView ? = null
    private var banglaRadioBtn:RadioButton ? = null
    private var englishRadioBtn:RadioButton ? = null
    private lateinit var locationswitch: SwitchMaterial
    private lateinit var currentLanguage:AppCompatTextView

    private var localLanguage = DeenSDKCore.GetDeenLanguage()

    private lateinit var viewmodel:SettingViewModel

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this)
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

        //initObserver()

        /*lifecycleScope.launch {
            viewmodel.getSetting()
        }*/

        languageLayout.setOnClickListener {
            showLanguiageDialog()
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
                is SettingResource.languageFailed -> requireContext().toast(localContext.getString(R.string.something_went_wrong_try_again))
                is SettingResource.settingData -> {

                    localLanguage = if(it.data?.language == "en")
                        "en"
                    else
                        "bn"


                    setupSettingDialogLanguage()

                    locationswitch.isChecked = it.data?.location_setting == false

                }

                /*is SettingResource.languageDataUpdate ->
                {
                    localLanguage = if(it.language == "en")
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
                }*/
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
            /*lifecycleScope.launch {
                viewmodel.updateSetting("bn")
            }*/
            localLanguage = "bn"
            changeLanguage(localLanguage)
            DeenSDKCore.DeenCallBackListener?.deenLanguageChangeListner(localLanguage)
            dialog?.dismiss()
            updateLanguage()
        }

        enLayout?.setOnClickListener {
            /*lifecycleScope.launch {
                viewmodel.updateSetting("en")
            }*/
            localLanguage = "en"
            changeLanguage(localLanguage)
            DeenSDKCore.DeenCallBackListener?.deenLanguageChangeListner(localLanguage)
            dialog?.dismiss()
            updateLanguage()
        }

    }


    fun updateLanguage() {

        val navController = findNavController()
        navController.popBackStack()
        navController.popBackStack()
        navController.popBackStack()
        navController.navigate(R.id.dashboardFakeFragment)
        navController.navigate(R.id.moreFragment)
        navController.navigate(R.id.settingFragment)
    }

    private fun setupSettingDialogLanguage()
    {

        //DeenSDKCore.GetDeenLanguage() = localLanguage

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