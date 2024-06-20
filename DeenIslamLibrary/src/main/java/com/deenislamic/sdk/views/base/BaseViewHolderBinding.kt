package com.deenislamic.sdk.views.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolderBinding(itemView: View?): RecyclerView.ViewHolder(itemView!!) {

    private var mCurrentPosition = 0
    private var view_type = 0

    open fun onBind(position: Int,viewtype:Int) {
        mCurrentPosition = position
        view_type = viewtype
    }

    open fun onBind(position: Int) {
        mCurrentPosition = position
    }

}