package com.deenislamic.sdk.views.ramadan.patch

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.ramadan.FastTime
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.views.adapters.ramadan.RamadanTimeListAdapter

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