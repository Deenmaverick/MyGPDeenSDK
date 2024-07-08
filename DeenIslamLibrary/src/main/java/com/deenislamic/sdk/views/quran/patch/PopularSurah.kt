package com.deenislamic.sdk.views.quran.patch;

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.adapters.quran.PopularSurahAdapter

internal class PopularSurah(itemView: View, items: List<Item>,isTitle:Boolean = true) {

    private val listView: RecyclerView = itemView.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private var popularSurahAdapter: PopularSurahAdapter

    init {

        if(isTitle){
            itemTitle.show()
        } else {
            (listView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
            itemTitle.hide()
        }

        itemTitle.text = itemTitle.context.getString(R.string.popular_sura)

        itemTitle.setPadding(12.dp,0,0,0)


        listView.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            popularSurahAdapter = PopularSurahAdapter()
            adapter = popularSurahAdapter
            popularSurahAdapter.update(items)

        }
    }
}