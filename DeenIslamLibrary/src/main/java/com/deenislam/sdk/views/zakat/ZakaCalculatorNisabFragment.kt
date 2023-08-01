package com.deenislam.sdk.views.zakat

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.deenislam.sdk.R
import com.deenislam.sdk.databinding.FragmentZakaCalculatorNisabBinding
import com.deenislam.sdk.service.callback.ZakatCalculatorCallback
import com.deenislam.sdk.utils.hideKeyboard
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseFragment

internal class ZakaCalculatorNisabFragment(
    private val callback: ZakatCalculatorCallback
) : BaseFragment<FragmentZakaCalculatorNisabBinding>(FragmentZakaCalculatorNisabBinding::inflate) {

    private val gold_price = 684675.0
    private val silver_price = 53550.0

    // data
    private var nisab_type = 1
    private var nisab_amount = gold_price

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.goldBtn.setOnClickListener {
            binding.goldBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.primary))
            binding.goldBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            binding.silverBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
            binding.silverBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
            binding.covertedNisab.text = "৳ $gold_price".numberLocale()
            nisab_type = 1
            nisab_amount = gold_price

        }

        binding.silverBtn.setOnClickListener {
            binding.goldBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
            binding.goldBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))
            binding.silverBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.primary))
            binding.silverBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            binding.covertedNisab.text = "৳ $silver_price".numberLocale()
            nisab_type = 2
            nisab_amount = silver_price
        }
    }

}