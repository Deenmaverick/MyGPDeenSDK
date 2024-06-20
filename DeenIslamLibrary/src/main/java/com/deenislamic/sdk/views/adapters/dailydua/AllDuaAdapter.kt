package com.deenislamic.sdk.views.adapters.dailydua;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dailydua.alldua.Data
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.RECYCLERFOOTER
import com.deenislamic.sdk.utils.RECYCLER_DATA_AVAILABLE
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class AllDuaAdapter(
    private val callback: AllDuaCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val categoryList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =

    when(viewType)
    {
        RECYCLER_DATA_AVAILABLE ->
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_all_dua, parent, false)
        )

        else ->
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.layout_footer, parent, false)
        )

    }

    fun update(data: List<Data>)
    {
        categoryList.clear()
        categoryList.addAll(data)
        notifyItemInserted(itemCount)
    }

    override fun getItemViewType(position: Int): Int =
        when (categoryList.size) {
            position -> RECYCLERFOOTER
            else -> RECYCLER_DATA_AVAILABLE
        }

    override fun getItemCount(): Int = categoryList.size+1

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val  catIcon:AppCompatImageView by lazy { itemView.findViewById(R.id.menuIcon) }
        private val catName:AppCompatTextView by lazy { itemView.findViewById(R.id.menuTitile) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                RECYCLER_DATA_AVAILABLE ->
                {
                    catName.text = categoryList[position].Category
                    catIcon.imageLoad(BASE_CONTENT_URL_SGP+categoryList[position].ImageUrl)

                    itemView.setOnClickListener {
                        callback.selectedCat(categoryList[position].Id,categoryList[position].Category)
                    }
                }
            }
        }

    }
}
internal interface AllDuaCallback
{
    fun selectedCat(id: Int, category: String)
}
