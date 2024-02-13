package com.deenislam.sdk.views.ramadan.patch

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.ramadan.FastTime
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.views.adapters.ramadan.RamadanTimeListAdapter

internal class RamadanTable(itemView: View, fastTime: List<FastTime>, dateArabic:String) {

    private val ramadanTimeList: RecyclerView = itemView.findViewById(R.id.ramadanTimeList)
    private val ramadanTimeSub:AppCompatTextView = itemView.findViewById(R.id.ramadanTimeSub)

    init {

        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 12.dp

        ramadanTimeSub.text = dateArabic
        ramadanTimeList.apply {
            adapter = RamadanTimeListAdapter(fastTime)
        }
    }
}