package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.ZakatResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.ZakatRepository
import com.deenislam.sdk.service.network.response.zakat.SavedZakatResponse
import kotlinx.coroutines.launch

internal class ZakatViewModel(
    private val repository: ZakatRepository
) : ViewModel() {

    private val _zakatLiveData:MutableLiveData<ZakatResource> = MutableLiveData()
    val zakatLiveData:MutableLiveData<ZakatResource> get() = _zakatLiveData

    private val _savedZakatLiveData:MutableLiveData<ZakatResource> = MutableLiveData()
    val savedZakatLiveData:MutableLiveData<ZakatResource> get() = _savedZakatLiveData

    private val _zakatNisabLiveData:MutableLiveData<ZakatResource> = MutableLiveData()
    val zakatNisabLiveData:MutableLiveData<ZakatResource> get() = _zakatNisabLiveData


    fun addZakatHistory(
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
    )
    {
        viewModelScope.launch {
            val response = repository.addZakatHistory(
                nisab_amount = nisab_amount,
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
                other_capital = other_capital,
                debt_to_family = debt_to_family,
                debt_to_others = debt_to_others,
                debt_credit_card_payment = debt_credit_card_payment,
                debt_home_payment = debt_home_payment,
                debt_car_payment = debt_car_payment,
                debt_business_payment = debt_business_payment,
                total_assets = total_assets,
                total_debts = total_debts,
                zakat_payable = zakat_payable
            )

            when(response)
            {
                is ApiResource.Failure -> _zakatLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _zakatLiveData.value = ZakatResource.zakatHistoryAdded
                    else
                        _zakatLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    fun updateZakatHistory(
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
    )
    {
        viewModelScope.launch {
            val response = repository.updateZakatHistory(
                nisab_amount = nisab_amount,
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
                other_capital = other_capital,
                debt_to_family = debt_to_family,
                debt_to_others = debt_to_others,
                debt_credit_card_payment = debt_credit_card_payment,
                debt_home_payment = debt_home_payment,
                debt_car_payment = debt_car_payment,
                debt_business_payment = debt_business_payment,
                total_assets = total_assets,
                total_debts = total_debts,
                zakat_payable = zakat_payable,
                id = id
            )

            when(response)
            {
                is ApiResource.Failure -> _zakatLiveData.value = ZakatResource.historyUpdateFailed
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _zakatLiveData.value = ZakatResource.historyUpdateSuccess
                    else
                        _zakatLiveData.value = ZakatResource.historyUpdateFailed
                }
            }
        }
    }

    // get saved zakat

    fun getSavedZakat(language:String)
    {
        viewModelScope.launch {
            processSavedZakatList(repository.getSavedZakatList(language) as ApiResource<SavedZakatResponse>)
        }
    }

    private fun processSavedZakatList(response: ApiResource<SavedZakatResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _savedZakatLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data.isNotEmpty())
                    _savedZakatLiveData.value = ZakatResource.savedZakatList(response.value.Data)
                else
                    _savedZakatLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun delHistory(delID: Int, adapterPosition: Int)
    {
        viewModelScope.launch {

            val response = repository.delHistory(delID)

            when(response)
            {
                is ApiResource.Failure -> _savedZakatLiveData.value = ZakatResource.historyDeleteFailed
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _savedZakatLiveData.value = ZakatResource.historyDeleted(adapterPosition)
                    else
                        _savedZakatLiveData.value =  ZakatResource.historyDeleteFailed
                }
            }
        }
    }

    fun getZakatNisab()
    {
        viewModelScope.launch {

            val response = repository.getZakatNisab()

            when(response)
            {
                is ApiResource.Failure -> _zakatNisabLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _zakatNisabLiveData.value = ZakatResource.zakatNisab(response.value.Data)
                    else
                        _zakatNisabLiveData.value =  CommonResource.API_CALL_FAILED
                }
            }
        }
    }


    fun clear()
    {
        _savedZakatLiveData.value = CommonResource.CLEAR
    }
}