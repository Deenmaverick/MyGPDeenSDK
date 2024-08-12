package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class ZakatRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun addZakatHistory(
        nisab_amount: Double,
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
        other_capital: Double,
        debt_to_family: Double,
        debt_to_others: Double,
        debt_credit_card_payment: Double,
        debt_home_payment: Double,
        debt_car_payment: Double,
        debt_business_payment: Double,
        total_assets:Double,
        total_debts:Double,
        zakat_payable:Double

    ) = makeApicall {

        deenService?.addZakatHistory(
            Nisab = nisab_amount,
            CashInHands = property_cash_in_hand,
            CashInBankAccount = property_cash_in_bank,
            GoldEquivalentamount = property_gold,
            SilverEquivalentamount = property_silver,
            InvestmentStockMarket = investment_stock_market,
            OtherInvestments = other_investment,
            PropertyValue = property_value,
            HouseRent = property_house_rent,
            CashinBusiness = cash_in_business,
            ProductinBusiness = cash_in_business_product,
            AgricultureAmount = agriculture_amount,
            PensionAmount = other_pension,
            OthercapitalAmount = other_capital,
            DebtsToFamily = debt_to_family,
            DebtsToOthers = debt_to_others,
            CreditCardPayment = debt_credit_card_payment,
            HomePayment = debt_home_payment,
            CarPayment = debt_car_payment,
            BusinessPayment = debt_business_payment,
            ZakatPayable = zakat_payable,
            TotalAssets = total_assets,
            DebtsAndLiabilities = total_debts
        )
    }

    suspend fun updateZakatHistory(
        nisab_amount: Double,
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
        other_capital: Double,
        debt_to_family: Double,
        debt_to_others: Double,
        debt_credit_card_payment: Double,
        debt_home_payment: Double,
        debt_car_payment: Double,
        debt_business_payment: Double,
        total_assets:Double,
        total_debts:Double,
        zakat_payable:Double,
        id:Int
    ) = makeApicall {

        deenService?.updateZakatHistory(
            Nisab = nisab_amount,
            CashInHands = property_cash_in_hand,
            CashInBankAccount = property_cash_in_bank,
            GoldEquivalentamount = property_gold,
            SilverEquivalentamount = property_silver,
            InvestmentStockMarket = investment_stock_market,
            OtherInvestments = other_investment,
            PropertyValue = property_value,
            HouseRent = property_house_rent,
            CashinBusiness = cash_in_business,
            ProductinBusiness = cash_in_business_product,
            AgricultureAmount = agriculture_amount,
            PensionAmount = other_pension,
            OthercapitalAmount = other_capital,
            DebtsToFamily = debt_to_family,
            DebtsToOthers = debt_to_others,
            CreditCardPayment = debt_credit_card_payment,
            HomePayment = debt_home_payment,
            CarPayment = debt_car_payment,
            BusinessPayment = debt_business_payment,
            ZakatPayable = zakat_payable,
            TotalAssets = total_assets,
            DebtsAndLiabilities = total_debts,
            Id = id
        )

    }
    suspend fun getSavedZakatList(language:String) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getSavedZakatList(requestBody)
    }

    suspend fun delHistory(delID:Int) = makeApicall {

        val body = JSONObject()
        body.put("id", delID)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.delZakatHistory(requestBody)

    }

    suspend fun getZakatNisab() = makeApicall {

        deenService?.getZakatNisab()

    }

    suspend fun getPatch(language:String) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getZakatPatch(parm = requestBody)

    }
} 