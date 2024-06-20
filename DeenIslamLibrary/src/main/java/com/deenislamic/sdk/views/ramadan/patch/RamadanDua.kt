package com.deenislamic.sdk.views.ramadan.patch;

import android.graphics.Typeface
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.ramadan.RamadanDua
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.setStarMargin
import com.deenislamic.sdk.views.adapters.ramadan.RamadanDuaListAdapter

internal class RamadanDua(itemView: View, ramadanDua: List<RamadanDua>) {

    private val icon: AppCompatImageView = itemView.findViewById(R.id.icon)
    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private val listview: RecyclerView = itemView.findViewById(R.id.listview)


    init {
        icon.hide()
        itemTitle.text = itemTitle.context.getString(R.string.necessary_duas)
        itemTitle.setStarMargin(1.dp)
        itemTitle.typeface = Typeface.DEFAULT

        listview.apply {
            setPadding(0,0,0,0)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            adapter = RamadanDuaListAdapter(ramadanDua)
        }
    }

}