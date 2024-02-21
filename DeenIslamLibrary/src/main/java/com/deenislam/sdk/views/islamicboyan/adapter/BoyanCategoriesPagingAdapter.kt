package com.deenislam.sdk.views.islamicboyan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.boyan.categoriespaging.Data
import com.deenislam.sdk.views.base.BaseViewHolder

internal class BoyanCategoriesPagingAdapter(
    private val callback: BoyanCategoriesCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val categoryList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_categories_full, parent, false)
        )

    fun update(data: List<Data>)
    {
        categoryList.clear()
        categoryList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int  = categoryList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val name: AppCompatTextView = itemView.findViewById(R.id.txtviw_categoryname)

        override fun onBind(position: Int) {
            super.onBind(position)

            val categoryItem = categoryList[position]

            name.text = categoryItem.category

            itemView.setOnClickListener {
                callback.chapterClick(categoryItem.Id)
            }
        }
    }
}

interface BoyanCategoriesCallback {
    fun chapterClick(boyanId: Int)
}