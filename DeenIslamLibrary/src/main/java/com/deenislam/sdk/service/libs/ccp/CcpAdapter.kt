package com.deenislam.sdk.service.libs.ccp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseViewHolder

internal class CcpAdapter(
    private val ccp_list:ArrayList<CcpModel>,
    private val callback:CcpAdapterCallback
): RecyclerView.Adapter<BaseViewHolder>(), Filterable {

    var ccp_filter: ArrayList<CcpModel> = ccp_list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
       ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ccp, parent, false)
        )

    override fun getItemCount(): Int = ccp_filter.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: View) :BaseViewHolder(itemView)
    {
        private val ic_flag:ImageView = itemView.findViewById(R.id.ic_flag)
        private val country_name:TextView = itemView.findViewById(R.id.country_name)
        private val country_code:TextView = itemView.findViewById(R.id.country_code)

        override fun onBind(position: Int) {
            super.onBind(position)
            ic_flag.setImageDrawable(AppCompatResources.getDrawable(itemView.context,ccp_filter[position].ic_flag))
            country_code.text = "+${ccp_filter[position].dialCode}"
            country_name.text = ccp_filter[position].countryName

            itemView.setOnClickListener {

                callback.selectedCountry(ccp_filter[position])
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) ccp_filter = ccp_list else {
                    val filteredList = ArrayList<CcpModel>()
                    ccp_list
                        .filter {
                            (it.countryName.lowercase().contains(constraint.toString().lowercase()))

                        }
                        .forEach { filteredList.add(it) }
                    ccp_filter = filteredList

                }
                return FilterResults().apply { values = ccp_filter }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                ccp_filter = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<CcpModel>

                notifyDataSetChanged()
            }
        }
    }
}

internal interface CcpAdapterCallback
{
    fun selectedCountry(country:CcpModel)
}