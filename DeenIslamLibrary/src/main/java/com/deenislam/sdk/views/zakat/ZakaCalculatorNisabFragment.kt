package com.deenislam.sdk.views.zakat

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.deenislam.sdk.R
import com.deenislam.sdk.databinding.FragmentZakaCalculatorNisabBinding
import com.deenislam.sdk.service.callback.ZakatCalculatorCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.ZakatResource
import com.deenislam.sdk.service.repository.ZakatRepository
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.viewmodels.ZakatViewModel
import com.deenislam.sdk.views.base.BaseFragment
import kotlinx.coroutines.launch

internal class ZakaCalculatorNisabFragment(
    private val callback: ZakatCalculatorCallback
) : BaseFragment<FragmentZakaCalculatorNisabBinding>(FragmentZakaCalculatorNisabBinding::inflate) {

    // data
    private var nisab_type = 1
    private var nisab_amount = 0.0

    private lateinit var viewModel: ZakatViewModel

    override fun OnCreate() {
        super.OnCreate()

        // init voiewmodel
        val repository = ZakatRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewModel = ZakatViewModel(repository)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingState()
        ViewCompat.setTranslationZ(binding.progressLayout.root, 10F)
        ViewCompat.setTranslationZ(binding.noInternetLayout.root, 10F)


        binding.nextBtn.setOnClickListener {
            requireContext().hideKeyboard(requireView())
            callback.nisabNextBtnClicked(nisab_type,nisab_amount)
        }

        // steps

        binding.step1.stepCount.text = localContext.getString(R.string.step,"1").numberLocale()
        binding.step1.contentTxt.text = localContext.getString(R.string.zakat_calculator_nisab_step1)

        binding.step2.stepCount.text =  localContext.getString(R.string.step,"2").numberLocale()
        binding.step2.contentTxt.text = localContext.getString(R.string.zakat_calculator_nisab_step2)

        binding.step3.stepCount.text =  localContext.getString(R.string.step,"3").numberLocale()
        binding.step3.contentTxt.text = localContext.getString(R.string.zakat_calculator_nisab_step3)

        binding.step4.stepCount.text =  localContext.getString(R.string.step,"4").numberLocale()
        binding.step4.contentTxt.text = localContext.getString(R.string.zakat_calculator_nisab_step4)

        binding.step5.stepCount.text =  localContext.getString(R.string.step,"5").numberLocale()
        binding.step5.contentTxt.text = localContext.getString(R.string.zakat_calculator_nisab_step5)

        initObserver()
        loadApiData()

        //click retry button for get api data again
        binding.noInternetLayout.noInternetRetry.setOnClickListener {
            loadingState()
            loadApiData()
        }


    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewModel.getZakatNisab()
        }
    }

    private fun initObserver()
    {
        viewModel.zakatNisabLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                //CommonResource.API_CALL_FAILED -> noInternetState()
                is ZakatResource.zakatNisab ->
                {

                    binding.progressLayout.root.hide()
                    binding.noInternetLayout.root.hide()

                    val nisabArray:ArrayList<String> = arrayListOf()

                    it.data.forEach {
                        nisab->
                        nisabArray.add(nisab.Product+"- ৳ "+nisab.ChargeAmount)
                    }

                    val adapter = ArrayAdapter(localContext, R.layout.custom_spinner_text, nisabArray)
                    //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.covertedNisab.adapter = adapter
                }

            }
        }

    }

    private fun loadingState()
    {
        binding.progressLayout.root.visible(true)
        binding.noInternetLayout.root.visible(false)
    }


    private fun noInternetState()
    {
        binding.progressLayout.root.hide()
        binding.noInternetLayout.root.show()
    }


}