package com.deenislamic.sdk.views.adapters.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.BasicCardListCallback
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.views.base.BaseViewHolder

private const val UNKNOWN = 0
private const val PRAYER_LEARN_SUB_CAT = 1
internal class BasicCardListAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private var subCatList:List<Data> = arrayListOf()
    private val callback = CallBackProvider.get<BasicCardListCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_basic_card_list, parent, false)
        )

    override fun getItemCount(): Int =
        if(subCatList.isNotEmpty())
            subCatList.size
    else 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }


    override fun getItemViewType(position: Int): Int {
        return if(subCatList.isNotEmpty())
            PRAYER_LEARN_SUB_CAT
        else
            UNKNOWN
    }

    fun update(data: List<Data>)
    {
        subCatList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val menuName: AppCompatTextView = itemView.findViewById(R.id.menuName)

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                PRAYER_LEARN_SUB_CAT ->
                {
                    //menuName.setPadding(0,0,0,0)
                    menuName.text = subCatList[absoluteAdapterPosition].Title

                    itemView.setOnClickListener {
                        callback?.basicCardListItemSelect(subCatList[absoluteAdapterPosition])
                    }
                }
            }
        }
    }
}