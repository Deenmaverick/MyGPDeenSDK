package com.deenislamic.sdk.views.adapters.islamicname;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.islamicname.IslamicnameAlphabetCallback
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class IslamicNameAlphabetAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var lastActive = 0
    private var callback = CallBackProvider.get<IslamicnameAlphabetCallback>()
    private val listOfAlphabet = arrayListOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_alphabet, parent, false)
        )

    override fun getItemCount(): Int = listOfAlphabet.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val tvAlphabetName: AppCompatTextView = itemView.findViewById(R.id.tvAlphabetName)
        override fun onBind(position: Int) {
            super.onBind(position)

            val listItem = listOfAlphabet.get(position)
            tvAlphabetName.text = listItem
            if (lastActive == absoluteAdapterPosition) {
                tvAlphabetName.setBackgroundResource(R.drawable.deen_ic_shape_oval)
                tvAlphabetName.setTextColor(
                    ContextCompat.getColor(
                        tvAlphabetName.context,
                        R.color.deen_primary
                    )
                )
            } else {
                tvAlphabetName.setBackgroundResource(R.drawable.deen_ic_shape_oval_transparent)
                tvAlphabetName.setTextColor(
                    ContextCompat.getColor(
                        tvAlphabetName.context,
                        R.color.deen_txt_ash
                    )
                )
            }


            itemView.setOnClickListener {
                val oldActive = lastActive
                lastActive = absoluteAdapterPosition
                notifyItemChanged(oldActive)
                notifyItemChanged(lastActive)
                callback = CallBackProvider.get<IslamicnameAlphabetCallback>()
                callback?.onAlphabetClick(listItem)
            }

        }
    }
}