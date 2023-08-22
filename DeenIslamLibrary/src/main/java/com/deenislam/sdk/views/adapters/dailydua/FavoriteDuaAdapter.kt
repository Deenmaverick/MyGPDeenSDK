package com.deenislam.sdk.views.adapters.dailydua;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dailydua.favdua.Data
import com.deenislam.sdk.views.base.BaseViewHolder

internal class FavoriteDuaAdapter(
    private val callback: FavDuaAdapterCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val favList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_daily_dua_favorite, parent, false)
        )

    fun update(data: List<Data>)
    {
        favList.clear()
        favList.addAll(data)
        notifyItemInserted(data.size-1)
    }

    fun delItem(position: Int)
    {
        favList.removeAt(if(position ==1)0 else position)
        notifyItemRemoved(position)
    }
    override fun getItemCount(): Int = favList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val itemTitle:AppCompatTextView = itemView.findViewById(R.id.itemTitle)
        private val duaTxt:AppCompatTextView = itemView.findViewById(R.id.duaTxt)
        private val surahInfo:AppCompatTextView = itemView.findViewById(R.id.surahInfo)
        private val rightBtn:AppCompatImageView = itemView.findViewById(R.id.rightBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            itemTitle.text = favList[position].SubCategoryName
            duaTxt.text = favList[position].Title

            rightBtn.setOnClickListener {
                callback.favClick(favList[position].DuaId,position)
            }

            itemView.setOnClickListener {
                callback.duaClick(favList[position].DuaId,favList[position].SubCategory,favList[position].SubCategoryName)
            }

        }
    }
}
internal interface FavDuaAdapterCallback
{
    fun favClick(duaID: Int, position: Int)
    fun duaClick(duaId: Int, subCategory: Int, category: String)
}
