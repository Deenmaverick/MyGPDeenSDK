package com.deenislamic.views.quran.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.views.adapters.quran.PopularSurahAdapter

internal class PopularSurah(itemView: View, items: List<Item>) {

    private val listView: RecyclerView = itemView.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    //private var popularSurahAdapter: PopularSurahAdapter

    init {

        itemTitle.text = itemTitle.context.getString(R.string.popular_sura)

        itemTitle.setPadding(12.dp,0,0,0)

        listView.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            /*popularSurahAdapter = PopularSurahAdapter()
            adapter = popularSurahAdapter
            popularSurahAdapter.update(items)*/

        }
    }
}