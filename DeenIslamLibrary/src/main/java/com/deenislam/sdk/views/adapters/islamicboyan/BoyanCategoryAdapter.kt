package com.deenislam.sdk.views.adapters.islamicboyan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.BoyanCategoryCardListCallback
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.views.base.BaseViewHolder

internal class BoyanCategoryAdapter(
    private val items: List<Item>
): RecyclerView.Adapter<BaseViewHolder>() {

    private var callback = CallBackProvider.get<BoyanCategoryCardListCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_categories, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val categoryHolder: AppCompatImageView by lazy { itemView.findViewById(R.id.cardviw_category) }
        private val textContent: AppCompatTextView by lazy { itemView.findViewById(R.id.txtviw_categoryname) }
        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = items[position]

            textContent.text = getData.Text

            itemView.setOnClickListener {
                callback = CallBackProvider.get<BoyanCategoryCardListCallback>()
                callback?.boyanCaregoryCardPatchItemClicked(getData)
            }
        }
    }


}