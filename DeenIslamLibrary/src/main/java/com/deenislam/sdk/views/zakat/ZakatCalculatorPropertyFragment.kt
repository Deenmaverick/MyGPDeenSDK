package com.deenislam.sdk.views.zakat

import android.os.Bundle
import android.view.View
import com.deenislam.sdk.databinding.FragmentZakatCalculatorPropertyBinding
import com.deenislam.sdk.service.callback.ZakatCalculatorCallback
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.hideKeyboard
import com.deenislam.sdk.views.base.BaseFragment
import com.google.android.material.textfield.TextInputEditText

internal class ZakatCalculatorPropertyFragment(
    private val callback: ZakatCalculatorCallback
) : BaseFragment<FragmentZakatCalculatorPropertyBinding>(FragmentZakatCalculatorPropertyBinding::inflate){

    private var property_cash_in_hand = 0.0
    private var property_cash_in_bank = 0.0
    private var property_gold = 0.0
    private var property_silver = 0.0
    private var investment_stock_market = 0.0
    private var other_investment = 0.0
    private var property_value = 0.0
    private var property_house_rent = 0.0
    private var cash_in_business = 0.0
    private var cash_in_business_product = 0.0
    private var agriculture_amount = 0.0
    private var other_pension = 0.0
    private var other_capital = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inputs
        binding.inputPart1.heading.text = "Cash (Savings & Bank)"
        binding.inputPart1.title1.text = "In Hands"
        binding.inputPart1.title2.text = "In Bank Account"

        binding.inputPart2.heading.text = "Gold & Silver (Equivalent amount)"
        binding.inputPart2.title1.text = "Gold"
        binding.inputPart2.title2.text = "Silver"

        binding.inputPart3.heading.text = "Investment"
        binding.inputPart3.title1.text = "Stock Market"
        binding.inputPart3.title2.text = "Other Investments"

        binding.inputPart4.heading.text = "Property"
        binding.inputPart4.title1.text = "Property Value"
        binding.inputPart4.title2.text = "House Rent"

        binding.inputPart5.heading.text = "Business"
        binding.inputPart5.title1.text = "Cash in Business"
        binding.inputPart5.title2.text = "Product"

        binding.inputPart6.heading.text = "Agriculture"
        binding.inputPart6.title1.text = "Amount"
        binding.inputPart6.title2.hide()
        binding.inputPart6.input2.hide()

        binding.inputPart7.heading.text = "Others"
        binding.inputPart7.title1.text = "Pension"
        binding.inputPart7.title2.text = "Other capital"


        binding.nextBtn.setOnClickListener {
            requireContext().hideKeyboard(requireView())

            // set data
            binding.inputPart1.input1.text.toString().apply {
                property_cash_in_hand = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart1.input2.text.toString().apply {
                property_cash_in_bank = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart2.input1.text.toString().apply {
                property_gold = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart2.input2.text.toString().apply {
                property_silver = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart3.input1.text.toString().apply {
                investment_stock_market = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart3.input2.text.toString().apply {
                other_investment = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart4.input1.text.toString().apply {
                property_value = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart4.input2.text.toString().apply {
                property_house_rent = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart5.input1.text.toString().apply {
                cash_in_business = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart5.input2.text.toString().apply {
                cash_in_business_product = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart6.input1.text.toString().apply {
                agriculture_amount = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart7.input1.text.toString().apply {
                other_pension = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }

            binding.inputPart7.input2.text.toString().apply {
                other_capital = if (this.isNotEmpty())
                    this.toDouble()
                else
                    0.0
            }
            callback.propertyNextBtnClicked(
                property_cash_in_hand = property_cash_in_hand,
                property_cash_in_bank = property_cash_in_bank,
                property_gold = property_gold,
                property_silver = property_silver,
                investment_stock_market = investment_stock_market,
                other_investment = other_investment,
                property_value = property_value,
                property_house_rent = property_house_rent,
                cash_in_business = cash_in_business,
                cash_in_business_product = cash_in_business_product,
                agriculture_amount = agriculture_amount,
                other_pension = other_pension,
                other_capital = other_capital
            )
        }

    }

    override fun onResume() {
        super.onResume()
        callback.getZakatData().let {
            updateInputValue(binding.inputPart1.input1,it.CashInHands)
            updateInputValue(binding.inputPart1.input2,it.CashInBankAccount)
            updateInputValue(binding.inputPart2.input1,it.GoldEquivalentamount)
            updateInputValue(binding.inputPart2.input2,it.SilverEquivalentamount)
            updateInputValue(binding.inputPart3.input1,it.InvestmentStockMarket)
            updateInputValue(binding.inputPart3.input2,it.OtherInvestments)
            updateInputValue(binding.inputPart4.input1,it.PropertyValue)
            updateInputValue(binding.inputPart4.input2,it.HouseRent)
            updateInputValue(binding.inputPart5.input1,it.CashinBusiness)
            updateInputValue(binding.inputPart5.input2,it.ProductinBusiness)
            updateInputValue(binding.inputPart6.input1,it.AgricultureAmount)
            updateInputValue(binding.inputPart7.input1,it.PensionAmount)
            updateInputValue(binding.inputPart7.input2,it.OthercapitalAmount)

        }
    }

    private fun updateInputValue(input: TextInputEditText, value: Double)
    {
       input.setText(if(value>0) value.toString() else "")

    }

}