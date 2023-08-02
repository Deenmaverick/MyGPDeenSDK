package com.deenislam.sdk.views.adapters.dailydua;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dailydua.alldua.Data
import com.deenislam.sdk.utils.BASE_CONTENT_URL
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.views.base.BaseViewHolder

internal class AllDuaAdapter(
    private val callback: AllDuaCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val categoryList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_all_dua, parent, false)
        )

    fun update(data: List<Data>)
    {
        categoryList.clear()
        categoryList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val  catIcon:AppCompatImageView =  itemView.findViewById(R.id.menuIcon)
        private val catName:AppCompatTextView = itemView.findViewById(R.id.menuTitile)

        override fun onBind(position: Int) {
            super.onBind(position)

            catName.text = categoryList[position].Category
            catIcon.load(BASE_CONTENT_URL+categoryList[position].ImageUrl)

            itemView.setOnClickListener {
                callback.selectedCat(categoryList[position].Id,categoryList[position].Category)
            }
        }
    }
}
interface AllDuaCallback
{
    fun selectedCat(id: Int, category: String)
}
