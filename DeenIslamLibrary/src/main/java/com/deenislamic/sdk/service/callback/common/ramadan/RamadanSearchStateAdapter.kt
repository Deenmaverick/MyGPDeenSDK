package com.deenislamic.sdk.service.callback.common.ramadan;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.RamadanCallback
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class RamadanSearchStateAdapter(
    private val stateArray: ArrayList<StateModel>,
    private var selectedState: StateModel?
) : RecyclerView.Adapter<BaseViewHolder>(),
    Filterable {

    var dataFilter : ArrayList<StateModel> = stateArray
    private val callback = CallBackProvider.get<RamadanCallback>()

    init {
        if(selectedState==null && stateArray.size>0)
            selectedState = stateArray[0]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_state, parent, false)
        )

    override fun getItemCount(): Int = dataFilter.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemName:AppCompatTextView = itemView.findViewById(R.id.itemName)
        private val ic_right:AppCompatImageView = itemView.findViewById(R.id.ic_right)
        private val border:View by lazy { itemView.findViewById(R.id.border) }

        override fun onBind(position: Int) {
            super.onBind(position)

            if(absoluteAdapterPosition == itemCount-1)
                border.hide()

            itemName.text = dataFilter[absoluteAdapterPosition].stateValue

            if(dataFilter[absoluteAdapterPosition].state == selectedState?.state) {
                itemName.setTextColor(ContextCompat.getColor(itemName.context,R.color.deen_primary))
                ic_right.show()
            }
            else {
                itemName.setTextColor(ContextCompat.getColor(itemName.context,R.color.deen_txt_ash))
                ic_right.hide()
            }

            itemView.setOnClickListener {
                callback?.stateSelected(dataFilter[absoluteAdapterPosition])
            }

        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                dataFilter = if (charString.isEmpty()) stateArray else {
                    val filteredList = ArrayList<StateModel>()
                    dataFilter
                        .filter {
                            (it.stateValue.lowercase().contains(constraint.toString().lowercase()))

                        }
                        .forEach { filteredList.add(it) }
                    filteredList

                }
                return FilterResults().apply { values = dataFilter }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                dataFilter = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<StateModel>

                notifyDataSetChanged()
            }
        }
    }
}