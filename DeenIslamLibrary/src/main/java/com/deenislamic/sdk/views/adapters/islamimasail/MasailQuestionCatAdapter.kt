package com.deenislamic.sdk.views.adapters.islamimasail;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamiMasailCallback
import com.deenislamic.sdk.service.network.response.islamimasail.catlist.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class MasailQuestionCatAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    // View types
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var isBottomLoading = false

    private var catdatalist: List<Data> = arrayListOf()

    private var callback = CallBackProvider.get<IslamiMasailCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder{

        val layout = when (viewType) {
            VIEW_TYPE_ITEM -> R.layout.item_hadith_chapter_by_collection
            VIEW_TYPE_LOADING -> R.layout.item_bottom_loading
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater
            .from(parent.context.getLocalContext())
            .inflate(layout, parent, false)

        return ViewHolder(view)
    }

    fun update(data: List<Data>) {
        val newData = ArrayList(catdatalist)
        newData.addAll(data)

        // Remove duplicates using a HashSet
        val uniqueData = newData.distinctBy { it.Id } // assuming 'id' is a unique identifier

        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                catdatalist,
                uniqueData
            )
        )
        catdatalist = ArrayList(uniqueData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setLoading(loading: Boolean) {
        if (isBottomLoading != loading) {
            isBottomLoading = loading
            if (loading) {
                // Show loading indicator
                notifyItemInserted(catdatalist.size)
            } else {
                // Hide loading indicator
                notifyItemRemoved(catdatalist.size)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < catdatalist.size) VIEW_TYPE_ITEM else VIEW_TYPE_LOADING
    }

    override fun getItemCount(): Int = catdatalist.size + if(isBottomLoading) 1 else 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val countTxt:AppCompatTextView by lazy {  itemView.findViewById(R.id.countTxt)}
        private val name:AppCompatTextView by lazy {  itemView.findViewById(R.id.name)}
        private val count:AppCompatTextView by lazy {   itemView.findViewById(R.id.count)}

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype){
                VIEW_TYPE_ITEM -> {

                    val getdata = catdatalist[position]
                    count.text = getdata.ECount.toString()
                    countTxt.text = (absoluteAdapterPosition+1).toString().numberLocale()
                    name.text = getdata.category

                    itemView.setOnClickListener {
                        callback = CallBackProvider.get<IslamiMasailCallback>()
                        callback?.masailCatClicked(getdata)
                    }

                }
            }
        }
    }

    internal class DataDiffCallback(
        private val oldList: List<Data>,
        private val newList: List<Data>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].Id == newList[newItemPosition].Id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}