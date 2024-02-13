package com.deenislam.sdk.views.ramadan.patch

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.ramadan.RamadanSearchStateAdapter
import com.deenislam.sdk.service.models.ramadan.StateModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

internal class StateList(
    private val itemView: View,
    private val stateArray: ArrayList<StateModel>
) {

    private val dropdownView: MaterialButton = itemView.findViewById(R.id.inf)
    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private lateinit var ramadanSearchStateAdapter: RamadanSearchStateAdapter
    private var selectedState:StateModel ? = null

    init {
        dropdownView.setOnClickListener {
            setupDialog()
        }
    }

    fun updateSelectedState(stateModel: StateModel)
    {
        selectedState = stateModel
        dropdownView.text = stateModel.stateValue
        dialog?.dismiss()
    }

     fun setupDialog()
    {
        val context = itemView.rootView.context
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = LayoutInflater.from(itemView.context).inflate(R.layout.dialog_surah_list, null, false)

        // Initialize and assign variable
        val userInput = customAlertDialogView.findViewById<TextInputEditText>(R.id.search_surah)
        val stateList = customAlertDialogView.findViewById<RecyclerView>(R.id.surahList)
        val dismissBtn = customAlertDialogView.findViewById<ImageButton>(R.id.closeBtn)
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.title)

        title.text = context.getString(R.string.select_a_district)

        ramadanSearchStateAdapter = RamadanSearchStateAdapter(stateArray,selectedState)

        stateList.adapter = ramadanSearchStateAdapter

        dismissBtn?.setOnClickListener {
            dialog?.dismiss()
        }

        userInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ramadanSearchStateAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // show dialog

        dialog = materialAlertDialogBuilder
            .setView(customAlertDialogView)
            .setCancelable(true)
            .show()
    }

}