package com.deenislamic.sdk.views.adapters.ramadan;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.ramadan.FastTime

internal class RamadanViewPagerAdapter(
    private val listOfSets: List<List<FastTime>>,
    private val isRamadan: Boolean = false
) : RecyclerView.Adapter<RamadanViewPagerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.listview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_viewpager_recylerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapter = RamadanTimeListAdapter(
            fastTime = listOfSets[position],
            isRamadan = isRamadan
        )
        holder.recyclerView.adapter = adapter
    }

    override fun getItemCount(): Int {
        return listOfSets.size
    }
}