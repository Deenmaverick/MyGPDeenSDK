package com.deenislam.views.zakat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.deenislam.sdk.R
import com.deenislam.sdk.databinding.FragmentZakatCalculatorLiabilityBinding
import com.deenislam.sdk.service.callback.ZakatCalculatorCallback
import com.deenislam.sdk.utils.hideKeyboard
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.base.BaseFragment
import com.google.android.material.textfield.TextInputEditText

internal class ZakatCalculatorLiabilityFragment(
    private val callback: ZakatCalculatorCallback
): BaseFragment<FragmentZakatCalculatorLiabilityBinding>(FragmentZakatCalculatorLiabilityBinding::inflate) {

    private var debt_to_family = 0.0
    private var debt_to_others = 0.0
    private var debt_credit_card_payment = 0.0
    private var debt_home_payment = 0.0
    private var debt_car_payment = 0.0
    private var debt_business_payment = 0.0

    override fun OnCreate() {
        super.OnCreate()
        isOnlyBack(true)

        setupBackPressCallback(this)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inputs
        binding.inputPart1.heading.text = localContext.getString(R.string.debts)
        binding.inputPart1.title1.text = localContext.getString(R.string.to_family)
        binding.inputPart1.title2.text = localContext.getString(R.string.to_others)
        binding.inputPart1.heading.setTextColor(ContextCompat.getColor(requireContext(),R.color.brand_error))

        binding.inputPart2.heading.text = localContext.getString(R.string.liabilities)
        binding.inputPart2.title1.text = localContext.getString(R.string.credit_card_payment)
        binding.inputPart2.title2.text = localContext.getString(R.string.home_payment)
        binding.inputPart2.title3.text = localContext.getString(R.string.car_payment)
        binding.inputPart2.title4.text = localContext.getString(R.string.business_payment)
        binding.inputPart2.heading.setTextColor(ContextCompat.getColor(requireContext(),R.color.brand_error))
        binding.inputPart2.title3.show()
        binding.inputPart2.input3.show()
        binding.inputPart2.title4.show()
        binding.inputPart2.input4.show()


        binding.nextBtn.setOnClickListener {
            // set data
            binding.inputPart1.input1.text.toString().apply {
                Log.e("debt_to_family", this)
                debt_to_family = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart1.input2.text.toString().apply {
                debt_to_others = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart2.input1.text.toString().apply {
                debt_credit_card_payment = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart2.input2.text.toString().apply {
                debt_home_payment = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart2.input3.text.toString().apply {
                debt_car_payment = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart2.input4.text.toString().apply {
                debt_business_payment = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }


            requireContext().hideKeyboard(requireView())

            callback.liabilityNextBtnClicked(
                debt_to_family = debt_to_family,
                debt_to_others = debt_to_others,
                debt_credit_card_payment = debt_credit_card_payment,
                debt_home_payment = debt_home_payment,
                debt_car_payment = debt_car_payment,
                debt_business_payment = debt_business_payment
            )
        }

    }

    override fun onResume() {
        super.onResume()
        callback.getZakatData().let {
            updateInputValue(binding.inputPart1.input1,it.DebtsToFamily)
            updateInputValue(binding.inputPart1.input2,it.DebtsToOthers)
            updateInputValue(binding.inputPart2.input1,it.CreditCardPayment)
            updateInputValue(binding.inputPart2.input2,it.HomePayment)
            updateInputValue(binding.inputPart2.input3,it.CarPayment)
            updateInputValue(binding.inputPart2.input4,it.BusinessPayment)
        }
    }

    private fun updateInputValue(input: TextInputEditText, value: Double)
    {
        input.setText(if(value>0) value.toString() else "")

    }

}