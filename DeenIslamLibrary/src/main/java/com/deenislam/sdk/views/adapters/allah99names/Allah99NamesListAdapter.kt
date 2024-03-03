package com.deenislam.sdk.views.adapters.allah99names;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.Allah99NameCallback
import com.deenislam.sdk.service.network.response.allah99name.Data
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder

internal class Allah99NamesListAdapter(private val namercList: RecyclerView) : RecyclerView.Adapter<BaseViewHolder>() {

    private val data: MutableList<Data> = mutableListOf()
    private val callback = CallBackProvider.get<Allah99NameCallback>()
    var hasMoreData = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_allah_99_names_list, parent, false)
        )

    fun update(data: List<Data>)
    {
     /*   this.data.clear()
        this.data.addAll(data)
        notifyItemRangeChanged(0,itemCount)
*/
        if (data.isEmpty()) {
            hasMoreData = false
        } else {
            //val startPosition = this.data.size
            this.data.addAll(data)
            notifyItemRangeChanged(itemCount, data.size)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val nameCount:AppCompatTextView = itemView.findViewById(R.id.nameCount)
        private val nameArabic:AppCompatTextView = itemView.findViewById(R.id.nameArabic)
        private val nameEnglish:AppCompatTextView = itemView.findViewById(R.id.nameEnglish)
        private val nameBangla:AppCompatTextView = itemView.findViewById(R.id.nameBangla)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata  = data[position]

            nameCount.text = "${getdata.Serial}/99".numberLocale()
            nameArabic.text = getdata.Arabic
            nameEnglish.text = getdata.Name
            nameBangla.text = getdata.Meaning

            itemView.setOnClickListener {
                callback?.allahNameClicked(position)
            }

            val isLastItem = position == itemCount - 1
            val isTotalCountOdd = itemCount % 2 != 0

            if (isLastItem && isTotalCountOdd) {
                // Assuming you're using a LinearLayout or similar as root for your item layout
                val layoutParams = itemView.layoutParams as GridLayoutManager.LayoutParams

                // Set width to half the RecyclerView width (or close to half, depending on margins, paddings, etc.)
                layoutParams.width = namercList.width / 2

                // Use margins to center the view (subtract any margins/paddings if needed)
                layoutParams.leftMargin = namercList.width / 4
                layoutParams.rightMargin = namercList.width / 4

                itemView.layoutParams = layoutParams
            } else {
                // Reset to original layout parameters if they've been changed
                val layoutParams = itemView.layoutParams as GridLayoutManager.LayoutParams
                layoutParams.width = GridLayoutManager.LayoutParams.MATCH_PARENT
                layoutParams.leftMargin = 8.dp
                layoutParams.rightMargin = 8.dp
                itemView.layoutParams = layoutParams
            }


        }
    }
}