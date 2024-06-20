package com.deenislamic.sdk.views.common

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.views.adapters.common.Common3DotMenuAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.ref.WeakReference

internal object Common3DotMenu {

    private var dialog: Dialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : WeakReference<View>


    fun <T> showDialog(
        context: Context,
        inflater: LayoutInflater,
        title: String,
        options: ArrayList<OptionList>,
        data: T
    ) {

        dialog?.dismiss()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.DeenMaterialAlertDialog_Rounded)
        customAlertDialogView = WeakReference(inflater.inflate(R.layout.item_quran_3dot_option, null, false))

        customAlertDialogView.get()?.let {
            val surahname: AppCompatTextView = it.findViewById(R.id.surahName)
            val surahAyath: AppCompatTextView = it.findViewById(R.id.surahAyath)
            val arbSurah: AppCompatTextView = it.findViewById(R.id.arbSurah)
            val optionList: RecyclerView = it.findViewById(R.id.optionList)

            surahname.text = title
            arbSurah.hide()
            surahAyath.hide()


            optionList.apply {
                adapter = Common3DotMenuAdapter(options,data)
            }

            dialog = materialAlertDialogBuilder
                .setView(it)
                .setCancelable(true)
                .show().apply {
                    window?.setGravity(Gravity.CENTER)
                }
        }

    }


    fun closeDialog(){
        dialog?.dismiss()
    }


}