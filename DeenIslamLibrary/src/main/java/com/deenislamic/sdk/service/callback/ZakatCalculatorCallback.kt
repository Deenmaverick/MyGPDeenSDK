package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.zakat.Data
import com.deenislamic.sdk.views.zakat.ZakatCalculatorSummeryFragment

internal interface ZakatCalculatorCallback {
    fun nisabNextBtnClicked(nisab_type: Int, nisab_amount: Double)
    fun propertyNextBtnClicked(
        property_cash_in_hand: Double,
        property_cash_in_bank: Double,
        property_gold: Double,
        property_silver: Double,
        investment_stock_market: Double,
        other_investment: Double,
        property_value: Double,
        property_house_rent: Double,
        cash_in_business: Double,
        cash_in_business_product: Double,
        agriculture_amount: Double,
        other_pension: Double,
        other_capital: Double
    )
    fun liabilityNextBtnClicked(
        debt_to_family: Double,
        debt_to_others: Double,
        debt_credit_card_payment: Double,
        debt_home_payment: Double,
        debt_car_payment: Double,
        debt_business_payment: Double
    )
    fun summeryNextBtnClicked()

    fun getTotalAssets():Double
    fun getTotalDebts():Double
    fun getPayableZakat():Double

    fun getUpdateMode():Boolean
    fun getZakatData(): Data
    fun saveZakatCalculation(callback: ZakatCalculatorSummeryFragment)
    fun updateZakatCalculation(callback: ZakatCalculatorSummeryFragment)
}