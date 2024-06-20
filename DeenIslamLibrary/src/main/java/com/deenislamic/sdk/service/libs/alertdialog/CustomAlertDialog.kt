package com.deenislamic.sdk.service.libs.alertdialog;

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.LoadingButton
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

internal class CustomAlertDialog {

    private var customDialogCallback: CustomDialogCallback? =null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private var dialog: AlertDialog? = null

    private var btn1Text = ""
    private var btn2Text = ""


    companion object {
        var instance: CustomAlertDialog? = null
    }

    fun getInstance(): CustomAlertDialog {
        if (instance == null)
            instance = CustomAlertDialog()

        return instance as CustomAlertDialog
    }

    fun setupDialog(
        callback: CustomDialogCallback,
        context: Context,
        btn1Text:String,
        btn2Text:String,
        titileText:String,
        subTitileText:String
    )
    {
        instance?.customDialogCallback = callback

        instance?.materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.DeenMaterialAlertDialog_rounded)
        instance?.customAlertDialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_tasbeeh_reset, null, false)

        instance?.btn1Text = btn1Text
        instance?.btn2Text = btn2Text
        val btn1 = instance?.customAlertDialogView?.findViewById<MaterialButton>(R.id.totalBtn)
        val btn2 = instance?.customAlertDialogView?.findViewById<MaterialButton>(R.id.todayBtn)

        btn1?.text = btn1Text
        btn2?.text = btn2Text

        btn1?.setOnClickListener {
            instance?.customDialogCallback?.clickBtn1()
        }

        btn2?.setOnClickListener {
            Log.e("CustomDCallback",instance?.customDialogCallback.toString())
            instance?.customDialogCallback?.clickBtn2()
        }


        val titile = instance?.customAlertDialogView?.findViewById<AppCompatTextView>(R.id.greeting)
        val subTitile = instance?.customAlertDialogView?.findViewById<AppCompatTextView>(R.id.hint)

        titile?.text = titileText
        subTitile?.text = subTitileText


    }

    fun getBtn1() = instance?.customAlertDialogView?.findViewById<MaterialButton>(R.id.totalBtn)
    fun getBtn2() = instance?.customAlertDialogView?.findViewById<MaterialButton>(R.id.todayBtn)


    fun showDialog(cancelable:Boolean = true)
    {
        val btn1 = instance?.customAlertDialogView?.findViewById<MaterialButton>(R.id.totalBtn)
        val btn2 = instance?.customAlertDialogView?.findViewById<MaterialButton>(R.id.todayBtn)

        btn1?.context?.let { LoadingButton().getInstance(it).removeLoader() }

        btn1?.isClickable = true
        btn2?.isClickable = true

        btn1?.text = instance?.btn1Text
        btn2?.text = instance?.btn2Text


        if(instance?.dialog?.isShowing == true)
            instance?.dialog?.dismiss()

        if(instance?.customAlertDialogView?.parent!=null)
            (instance?.customAlertDialogView?.parent as ViewGroup).removeView(instance?.customAlertDialogView)

        instance?.dialog = instance?.materialAlertDialogBuilder
            ?.setView(customAlertDialogView)
            ?.setCancelable(cancelable)
            ?.show()?.apply {
                window?.setGravity(Gravity.BOTTOM)
            }
    }

    fun dismissDialog()
    {
        instance?.dialog?.dismiss()
    }
}

internal interface CustomDialogCallback
{
    fun clickBtn1()
    fun clickBtn2()
}
