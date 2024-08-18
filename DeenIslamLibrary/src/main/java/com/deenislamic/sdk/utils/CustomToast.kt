package com.deenislamic.sdk.utils

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.deenislamic.sdk.R
import com.google.android.material.snackbar.Snackbar

internal object CustomToast {

    fun show(message: String, iconResId: Int = 0, context: Context, anchorView: View) {
        val snackbar = Snackbar.make(anchorView, "", Snackbar.LENGTH_SHORT) // Empty message since we're not using the default text

        val snackbarView = snackbar.view
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.visibility = View.GONE // Set to GONE to remove it from the layout
        snackbar.view.setBackgroundColor(Color.TRANSPARENT) // Ensure transparent to use custom background

        // Inflate and add your custom view
        val inflater = LayoutInflater.from(context)
        val customLayout: View = inflater.inflate(R.layout.item_deen_gp_custom_toast, null)

        // Set your custom message and icon
        val icon: AppCompatImageView = customLayout.findViewById(R.id.icon)
        val toastMessage: AppCompatTextView = customLayout.findViewById(R.id.message)
        val closeBtn: AppCompatImageView = customLayout.findViewById(R.id.closeBtn)

        if (iconResId > 0) {
            icon.setImageResource(iconResId)
            icon.show()
        }else
            icon.hide()

        toastMessage.text = message

        // Adjust the layout parameters to ensure the custom layout fits correctly
        customLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Add the custom layout to the Snackbar's view hierarchy
        val snackbarLayout = snackbarView as ViewGroup
        snackbarLayout.addView(customLayout, 0)

        // Show the Snackbar
        snackbar.show()

        // Dismiss Snackbar when close button is clicked
        closeBtn.setOnClickListener {
            snackbar.dismiss()
        }
    }
}


