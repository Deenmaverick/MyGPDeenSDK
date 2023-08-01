package com.deenislam.sdk.views.zakat

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.ZakatCalculatorCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.ZakatResource
import com.deenislam.sdk.service.network.response.zakat.Data
import com.deenislam.sdk.service.repository.ZakatRepository
import com.deenislam.sdk.utils.reduceDragSensitivity
import com.deenislam.sdk.utils.toast
import com.deenislam.sdk.viewmodels.ZakatViewModel
import com.deenislam.sdk.views.adapters.MainViewPagerAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.views.zakat.ZakatCalculatorLiabilityFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch


internal class ZakatCalculatorFragment : BaseRegularFragment(), ZakatCalculatorCallback {

    private lateinit var viewmodel: ZakatViewModel

    private lateinit var nisabBtn:MaterialButton
    private lateinit var propertyBtn:MaterialButton
    private lateinit var liabilityBtn:MaterialButton
    private lateinit var summeryBtn:MaterialButton

    private lateinit var _viewPager: ViewPager2
    private lateinit var actionbar: ConstraintLayout
    private lateinit var header: LinearLayout

    private lateinit var mPageDestination: ArrayList<Fragment>
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerPosition:Int = 0

    private var callback: ZakatCalculatorFragmentCallback? =null

    private val args:ZakatCalculatorFragmentArgs by navArgs()

    private var updateMode:Boolean = false

    // all inpute and data
    private var zakat_id = -1
    private var nisab_type = 1
    private var nisab_amount = 0.0

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

    private var debt_to_family = 0.0
    private var debt_to_others = 0.0
    private var debt_credit_card_payment = 0.0
    private var debt_home_payment = 0.0
    private var debt_car_payment = 0.0
    private var debt_business_payment = 0.0

    private var payable_zakat = 0.0
    private var total_assets = 0.0
    private var total_debts = 0.0

    override fun OnCreate() {
        super.OnCreate()
        isOnlyBack(true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

        // init voiewmodel
        val repository = ZakatRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = ZakatViewModel(repository)

        args.zakatData?.let {
            Log.e("CashInHands",it.CashInHands.toString())
                nisab_amount = it.Nisab
                property_cash_in_hand = it.CashInHands
                property_cash_in_bank = it.CashInBankAccount
                property_gold = it.GoldEquivalentamount
                property_silver = it.SilverEquivalentamount
                investment_stock_market = it.InvestmentStockMarket
                other_investment = it.OtherInvestments
                property_value = it.PropertyValue
                property_house_rent = it.HouseRent
                cash_in_business = it.CashinBusiness
                cash_in_business_product = it.ProductinBusiness
                agriculture_amount = it.AgricultureAmount
                other_pension = it.PensionAmount
                other_capital = it.OthercapitalAmount

                debt_to_family = it.DebtsToFamily
                debt_to_others = it.DebtsToOthers
                debt_credit_card_payment = it.CreditCardPayment
                debt_home_payment = it.HomePayment
                debt_car_payment = it.CarPayment
                debt_business_payment = it.BusinessPayment

                payable_zakat = it.ZakatPayable
                total_assets = it.TotalAssets
                total_debts = it.DebtsAndLiabilities
                zakat_id = it.Id
                updateMode = true
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_zakat_calculator,container,false)

        //init view
        _viewPager = mainView.findViewById(R.id.viewPager)
        nisabBtn = mainView.findViewById(R.id.nisabBtn)
        propertyBtn = mainView.findViewById(R.id.propertyBtn)
        liabilityBtn = mainView.findViewById(R.id.liabilityBtn)
        summeryBtn = mainView.findViewById(R.id.summeryBtn)
        actionbar = mainView.findViewById(R.id.actionbar)
        header = mainView.findViewById(R.id.header)


        setupActionForOtherFragment(0,0,null,localContext.getString(R.string.zakat_calculation),true,mainView)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()

        mPageDestination = arrayListOf(
            ZakaCalculatorNisabFragment(this@ZakatCalculatorFragment),
            ZakatCalculatorPropertyFragment(this@ZakatCalculatorFragment),
            ZakatCalculatorLiabilityFragment(this@ZakatCalculatorFragment),
            ZakatCalculatorSummeryFragment(this@ZakatCalculatorFragment)
        )

        mainViewPagerAdapter = MainViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            mPageDestination
        )

        header.post {

            val param = _viewPager.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = header.height + actionbar.height

            _viewPager.layoutParams = param
        }

        _viewPager.apply {
            adapter = mainViewPagerAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isNestedScrollingEnabled = false
            }
            isUserInputEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            reduceDragSensitivity(2)
        }

        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                viewPagerPosition = position

                clearAllBtnSelection()

                when(position)
                {
                    0->
                    {
                        nisabBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),
                            R.color.card_bg))
                        nisabBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
                    }

                    1->
                    {
                        propertyBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.card_bg))
                        propertyBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))

                    }

                    2->
                    {
                        liabilityBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.card_bg))
                        liabilityBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
                    }

                    3->
                    {
                        summeryBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.card_bg))
                        summeryBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))

                    }

                }

            }
        })

    }

    private fun initObserver()
    {
        viewmodel.zakatLiveData.observe(viewLifecycleOwner)
        {

            when(it)
            {
                CommonResource.API_CALL_FAILED ->
                {
                    callback?.zakatAPIResponse(false)
                    requireContext().toast("Failed to save this calculation!")
                }
                ZakatResource.zakatHistoryAdded ->
                {
                    callback?.zakatAPIResponse(true)
                    requireContext().toast("Calculation has been saved")
                }

                ZakatResource.historyUpdateFailed ->
                {
                    callback?.zakatAPIResponse(false)
                    requireContext().toast("Failed to update this calculation")
                }

                ZakatResource.historyUpdateSuccess ->
                {
                    callback?.zakatAPIResponse(true)
                    requireContext().toast("Calculation has been updated")
                }
            }
        }
    }

    private fun clearAllBtnSelection()
    {
        nisabBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
        nisabBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))

        propertyBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
        propertyBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))

        liabilityBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
        liabilityBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))

        summeryBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
        summeryBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.txt_ash))

    }

    override fun nisabNextBtnClicked(nisab_type: Int, nisab_amount: Double) {
        this.nisab_type = nisab_type
        this.nisab_amount = nisab_amount
        _viewPager.setCurrentItem(1,false)
    }

    override fun propertyNextBtnClicked(
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
    ) {

        this.property_cash_in_hand = property_cash_in_hand
        this.property_cash_in_bank = property_cash_in_bank
        this.property_gold = property_gold
        this.property_silver = property_silver
        this.investment_stock_market = investment_stock_market
        this.other_investment = other_investment
        this.property_value = property_value
        this.property_house_rent = property_house_rent
        this.cash_in_business = cash_in_business
        this.cash_in_business_product = cash_in_business_product
        this.agriculture_amount = agriculture_amount
        this.other_pension = other_pension
        this.other_capital = other_capital

        _viewPager.setCurrentItem(2,false)
    }

    override fun liabilityNextBtnClicked(
        debt_to_family: Double,
        debt_to_others: Double,
        debt_credit_card_payment: Double,
        debt_home_payment: Double,
        debt_car_payment: Double,
        debt_business_payment: Double
    ) {

        this.debt_to_family = debt_to_family
        this.debt_to_others = debt_to_others
        this.debt_credit_card_payment = debt_credit_card_payment
        this.debt_home_payment = debt_home_payment
        this.debt_car_payment = debt_car_payment
        this.debt_business_payment = debt_business_payment

        calculate_zakat().apply {
            _viewPager.setCurrentItem(3,false)
        }
    }

    override fun summeryNextBtnClicked() {
        zakat_id = -1
        nisab_type = 1
        nisab_amount = 0.0

        property_cash_in_hand = 0.0
        property_cash_in_bank = 0.0
        property_gold = 0.0
        property_silver = 0.0
        investment_stock_market = 0.0
        other_investment = 0.0
        property_value = 0.0
        property_house_rent = 0.0
        cash_in_business = 0.0
        cash_in_business_product = 0.0
        agriculture_amount = 0.0
        other_pension = 0.0
        other_capital = 0.0

        debt_to_family = 0.0
        debt_to_others = 0.0
        debt_credit_card_payment = 0.0
        debt_home_payment = 0.0
        debt_car_payment = 0.0
        debt_business_payment = 0.0

        payable_zakat = 0.0
        total_assets = 0.0
        total_debts = 0.0

        updateMode = false
        _viewPager.setCurrentItem(0,false)
    }

    override fun getTotalAssets(): Double = total_assets

    override fun getTotalDebts(): Double = total_debts

    override fun getPayableZakat(): Double = payable_zakat
    override fun getUpdateMode(): Boolean  = updateMode

    override fun getZakatData(): Data = Data(
        AgricultureAmount = agriculture_amount,
        BusinessPayment = debt_business_payment,
            CarPayment = debt_car_payment,
            CashInBankAccount = property_cash_in_bank,
            CashInHands = property_cash_in_hand,
            CashinBusiness =cash_in_business,
            CreditCardPayment = debt_credit_card_payment,
            DebtsAndLiabilities = total_debts,
            DebtsToFamily = debt_to_family,
            DebtsToOthers = debt_to_others,
            EntryDate= "",
        GoldEquivalentamount = property_gold,
        HomePayment = debt_home_payment,
            HouseRent = property_house_rent,
            Id =0,
        InvestmentStockMarket = investment_stock_market,
        MSISDN ="",
        Nisab = nisab_amount,
        OtherInvestments = other_investment,
            OthercapitalAmount = other_capital,
            PensionAmount = other_pension,
            ProductinBusiness = cash_in_business_product,
            PropertyValue = property_value,
            SilverEquivalentamount = property_silver,
            TotalAssets = total_assets,
            ZakatPayable = payable_zakat,
            isactive = false,
            language = "en"
    )

    override fun saveZakatCalculation(callback: ZakatCalculatorSummeryFragment) {

        this.callback = callback

        lifecycleScope.launch {
            viewmodel.addZakatHistory(
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
                zakat_payable = payable_zakat
            )
        }
    }

    override fun updateZakatCalculation(callback: ZakatCalculatorSummeryFragment) {
        this.callback = callback
        lifecycleScope.launch {
            viewmodel.updateZakatHistory(
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
                zakat_payable = payable_zakat,
                id = zakat_id
            )
        }
    }

    private fun calculate_zakat()
    {
        total_assets = property_cash_in_hand+property_cash_in_bank+property_gold+
                property_silver+investment_stock_market+other_investment+property_value+
                property_house_rent+cash_in_business+cash_in_business_product+agriculture_amount+
                other_pension+other_capital

        total_debts = debt_to_family+debt_to_others+debt_credit_card_payment+debt_home_payment+
                debt_car_payment+debt_business_payment

        payable_zakat = if(total_assets>total_debts)
            (total_assets-total_debts)
        else
            0.0

        payable_zakat = if(payable_zakat>nisab_amount)
            (payable_zakat*2.5)/100
        else
            0.0

    }

}
interface ZakatCalculatorFragmentCallback
{
    fun zakatAPIResponse(success: Boolean)
}
