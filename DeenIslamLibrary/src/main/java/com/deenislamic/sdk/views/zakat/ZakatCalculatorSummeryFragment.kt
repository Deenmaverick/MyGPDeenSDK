package com.deenislamic.sdk.views.zakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.ZakatCalculatorCallback
import com.deenislamic.sdk.utils.LoadingButton
import com.deenislamic.sdk.utils.hideKeyboard
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

internal class ZakatCalculatorSummeryFragment(
    private val callback: ZakatCalculatorCallback
): BaseRegularFragment(), ZakatCalculatorFragmentCallback {

    private lateinit var nextBtn:MaterialButton
    private lateinit var saveBtn:MaterialButton
    private lateinit var payableAmount:AppCompatTextView
    private lateinit var totalAssetsAmount:AppCompatTextView
    private lateinit var debtAmount:AppCompatTextView
    private var updateMode:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_zakat_calculator_summery, container, false)

        //init view
        nextBtn = mainview.findViewById(R.id.nextBtn)
        saveBtn = mainview.findViewById(R.id.saveBtn)
        payableAmount = mainview.findViewById(R.id.payableAmount)
        totalAssetsAmount = mainview.findViewById(R.id.totalAssetsAmount)
        debtAmount = mainview.findViewById(R.id.debtAmount)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decimalFormat = DecimalFormat("0.00")

        payableAmount.text = "৳${decimalFormat.format(callback.getPayableZakat())}".numberLocale()
        totalAssetsAmount.text = "৳${decimalFormat.format(callback.getTotalAssets())}".numberLocale()
        debtAmount.text = "৳${decimalFormat.format(callback.getTotalDebts())}".numberLocale()

        nextBtn.setOnClickListener {
            requireContext().hideKeyboard(requireView())
            callback.summeryNextBtnClicked()
        }

        saveBtn.setOnClickListener {
            saveBtn.text = LoadingButton().getInstance(requireContext()).loader(saveBtn,R.color.deen_primary)
            if(updateMode)
            callback.updateZakatCalculation(this@ZakatCalculatorSummeryFragment)
            else
                callback.saveZakatCalculation(this@ZakatCalculatorSummeryFragment)

        }
    }

    override fun onResume() {
        super.onResume()
        updateMode = callback.getUpdateMode()

        if(updateMode)
            saveBtn.text = localContext.getString(R.string.update_the_calculation)
        else
            saveBtn.text = localContext.getString(R.string.save_the_calculation)
    }

    override fun zakatAPIResponse(success: Boolean) {
        LoadingButton().getInstance(requireContext()).removeLoader()
        saveBtn.text = if(updateMode)
             localContext.getString(R.string.update_the_calculation)
        else
             localContext.getString(R.string.save_the_calculation)
        saveBtn.isEnabled = !success
    }

}