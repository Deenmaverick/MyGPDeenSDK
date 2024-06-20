package com.deenislamic.sdk.service.callback

import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.service.network.response.allah99name.Data

internal interface Allah99NameCallback {

    fun allah99NameNextClicked(viewHolder: RecyclerView.ViewHolder)
    {

    }
    fun allah99NamePrevClicked(viewHolder: RecyclerView.ViewHolder)
    {

    }
    fun allahNameClicked(position: Int)
    {

    }
    fun allahNamePlayClicked(namedata: Data, absoluteAdapterPosition: Int)
    {

    }

    fun allahNamePauseClicked()
    {

    }
}