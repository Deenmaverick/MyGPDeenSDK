package com.deenislam.sdk.views.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.LocaleUtil
import java.util.Locale

internal abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var mCurrentPosition = 0
    private var view_type = 0


    open fun onBind(position: Int,viewtype:Int) {
        mCurrentPosition = position
         view_type = viewtype
    }

    open fun onBind(position: Int) {
        mCurrentPosition = position
    }

    fun getCurrentPosition(): Int {
        return mCurrentPosition
    }
}