package com.deenislamic.sdk.views.islamimasaIl.patch

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.network.response.islamimasail.questionbycat.Data
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.views.adapters.islamimasail.MasailQuestionListAdapter


internal class LastQuesntions(
    view: View,
    private val title: String,
    private val items: List<Item>,
    private val customviewtype:Int = 0
) {

    private  val listView: RecyclerView = view.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = view.findViewById(R.id.itemTitle)
    private val masailQuestionListAdapter = MasailQuestionListAdapter(customviewtype)

    fun load()
    {

        itemTitle.text = title

        //(itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp

        listView.apply {
            layoutManager = LinearLayoutManager(itemTitle.context,if(customviewtype >0) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,false)
            setPadding(0,0,0,0)
            adapter = masailQuestionListAdapter
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }

        masailQuestionListAdapter.update(items.map { toQuestiondata(it) })
    }

    private fun toQuestiondata(
        item: Item
    ): Data {
        return Data(
            Id = item.Id,
            IsFavorite = false,
            Place = "",
            QRaiserName = item.ArabicText,
            categoryId = item.CategoryId,
            categoryName = item.Reference,
            contenturl = item.contentBaseUrl,
            favCount = 0,
            imageurl = item.imageurl1,
            isAnonymous = false,
            isUrgent = false,
            language = item.Language,
            msisdn = "",
            title = item.Text,
            viewCount = 0
        )
    }
}