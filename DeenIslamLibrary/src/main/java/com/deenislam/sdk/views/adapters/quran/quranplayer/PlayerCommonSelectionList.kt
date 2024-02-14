package com.deenislam.sdk.views.adapters.quran.quranplayer;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.models.quran.quranplayer.PlayerCommonSelectionData
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView

internal class PlayerCommonSelectionList(
    private var commonData: ArrayList<PlayerCommonSelectionData> = arrayListOf(),
    private val callback: PlayerCommonSelectionListCallback
    ) : RecyclerView.Adapter<BaseViewHolder>() {
    fun update(data:List<PlayerCommonSelectionData>)
    {
        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                commonData,
                data
            )
        )
        commonData = data as ArrayList<PlayerCommonSelectionData>
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData() = commonData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_muezzin, parent, false)
        )

    override fun getItemCount(): Int = commonData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val shapeableImageView:ShapeableImageView by lazy { itemView.findViewById(R.id.shapeableImageView) }
        private val title:AppCompatTextView by lazy { itemView.findViewById(R.id.title) }
        private val icPlayPause:AppCompatImageView by lazy { itemView.findViewById(R.id.icPlayPause) }
        private val notification_only_btn:RadioButton by lazy { itemView.findViewById(R.id.notification_only_btn) }

        override fun onBind(position: Int) {
            super.onBind(position)
            val data = commonData[position]

            if(data.imageurl==null) {
                shapeableImageView.hide()
                title.setPadding(8.dp,8.dp,8.dp,8.dp)
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = 8.dp
            }

            icPlayPause.hide()
            title.text = data.title

            notification_only_btn.isChecked = data.isSelected

            itemView.setOnClickListener {
                callback.playerCommonListSelected(commonData[absoluteAdapterPosition],this@PlayerCommonSelectionList)
            }

        }
    }

    internal interface PlayerCommonSelectionListCallback
    {
        fun playerCommonListSelected(
            data: PlayerCommonSelectionData,
            adapter: PlayerCommonSelectionList
        )
    }

    internal class DataDiffCallback(
        private val oldList: List<PlayerCommonSelectionData>,
        private val newList: List<PlayerCommonSelectionData>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldList[oldItemPosition].Id == newList[newItemPosition].Id)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}