package com.deenislam.sdk.views.adapters.common

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

internal class CommonStateList(
    private val itemView: View
) {

    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private lateinit var ramadanSearchStateAdapter: RamadanSearchStateAdapter
    private var selectedState:StateModel ? = null
    private var stateArray:ArrayList<StateModel>  = arrayListOf(
        StateModel("dhaka", "Dhaka (ঢাকা)"),
        StateModel("barisal", "Barisal (বরিশাল)"),
        StateModel("khulna", "Khulna (খুলনা)"),
        StateModel("chittagong", "Chittagong (চট্টগ্রাম)"),
        StateModel("mymensingh", "Mymensingh (ময়মনসিংহ)"),
        StateModel("rangpur", "Rangpur (রংপুর)"),
        StateModel("rajshahi", "Rajshahi (রাজশাহী)"),
        StateModel("sylhet", "Sylhet (সিলেট)"),
        StateModel("bagerhat", "Bagerhat (বাগেরহাট)"),
        StateModel("chuadanga", "Chuadanga (চুয়াডাঙ্গা)"),
        StateModel("jessore", "Jessore (যশোর)"),
        StateModel("jhenaidah", "Jhenaidah (ঝিনাইদহ)"),
        StateModel("kushtia", "Kushtia (কুষ্টিয়া)"),
        StateModel("magura", "Magura (মাগুরা)"),
        StateModel("meherpur", "Meherpur (মেহেরপুর)"),
        StateModel("narail", "Narail (নড়াইল)"),
        StateModel("satkhira", "Satkhira (সাতক্ষীরা)"),
        StateModel("bandarban", "Bandarban (বান্দরবান)"),
        StateModel("brahmanbaria", "Brahmanbaria (ব্রাহ্মণবাড়িয়া)"),
        StateModel("chandpur", "Chandpur (চাঁদপুর)"),
        StateModel("comilla", "Comilla (কুমিল্লা)"),
        StateModel("coxsBazar", "CoxsBazar (কক্সবাজার)"),
        StateModel("feni", "Feni (ফেনী)"),
        StateModel("khagrachhari", "Khagrachhari (খাগড়াছড়ি)"),
        StateModel("lakshmipur", "Lakshmipur (লক্ষ্মীপুর)"),
        StateModel("noakhali", "Noakhali (নোয়াখালী)"),
        StateModel("rangamati", "Rangamati (রাঙ্গামাটি)"),
        StateModel("faridpur", "Faridpur (ফরিদপুর)"),
        StateModel("tangail", "Tangail (টাঙ্গাইল)"),
        StateModel("gazipur", "Gazipur (গাজীপুর)"),
        StateModel("gopalganj", "Gopalganj (গোপালগঞ্জ)"),
        StateModel("kishoreganj", "Kishoreganj (কিশোরগঞ্জ)"),
        StateModel("madaripur", "Madaripur (মাদারীপুর)"),
        StateModel("manikganj", "Manikganj (মানিকগঞ্জ)"),
        StateModel("munshiganj", "Munshiganj (মুন্সীগঞ্জ)"),
        StateModel("narayanganj", "Narayanganj (নারায়ণগঞ্জ)"),
        StateModel("narsingdi", "Narsingdi (নরসিংদী)"),
        StateModel("rajbari", "Rajbari (রাজবাড়ী)"),
        StateModel("shariatpur", "Shariatpur (শরীয়তপুর)"),
        StateModel("barguna", "Barguna (বরগুনা)"),
        StateModel("bhola", "Bhola (ভোলা)"),
        StateModel("jhalokati", "Jhalokati (ঝালকাঠি)"),
        StateModel("patuakhali", "Patuakhali (পটুয়াখালী)"),
        StateModel("pirojpur", "Pirojpur (পিরোজপুর)"),
        StateModel("dinajpur", "Dinajpur (দিনাজপুর)"),
        StateModel("gaibandha", "Gaibandha (গাইবান্ধা)"),
        StateModel("kurigram", "Kurigram (কুড়িগ্রাম)"),
        StateModel("lalmonirhat", "Lalmonirhat (লালমনিরহাট)"),
        StateModel("nilphamari", "Nilphamari (নীলফামারী)"),
        StateModel("panchagarh", "Panchagarh (পঞ্চগড়)"),
        StateModel("thakurgaon", "Thakurgaon (ঠাকুরগাঁও)"),
        StateModel("bogra", "Bogra (বগুড়া)"),
        StateModel("pabna", "Pabna (পাবনা)"),
        StateModel("joypurhat", "Joypurhat (জয়পুরহাট)"),
        StateModel("chapainawabganj", "Chapainawabganj (চাঁপাইনবাবগঞ্জ)"),
        StateModel("naogaon", "Naogaon (নওগাঁ)"),
        StateModel("natore", "Natore (নাটোর)"),
        StateModel("sirajganj", "Sirajganj (সিরাজগঞ্জ)"),
        StateModel("habiganj", "Habiganj (হবিগঞ্জ)"),
        StateModel("moulvibazar", "Moulvibazar (মৌলভীবাজার)"),
        StateModel("sunamganj", "Sunamganj (সুনামগঞ্জ)"),
        StateModel("sherpur", "Sherpur (শেরপুর)"),
        StateModel("jamalpur", "Jamalpur (জামালপুর)"),
        StateModel("netrokona", "Netrokona (নেত্রকোনা)")
    )
    private var customTitle:String?=null

    init {
        itemView.setOnClickListener {
            setupDialog()
        }

    }

    fun setCustomState(customState: List<StateModel>) {
        stateArray.clear()
        stateArray.addAll(customState)
        if(customState.isNotEmpty())
            selectedState =  customState[0]
    }

    fun setCustomTitle(title: String) {
        customTitle = title
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
        val title: AppCompatTextView = customAlertDialogView.findViewById(R.id.pageTitle)

        title.text = customTitle?.let { it }?:context.getString(R.string.select_a_district)

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

    fun stateSelected(stateModel: StateModel) {
        selectedState = stateModel
        dialog?.dismiss()
    }

    fun getSelectedState() = selectedState

}