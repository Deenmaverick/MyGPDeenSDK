package com.deenislam.sdk.views.adapters.subscription;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.SubscriptionCallback
import com.deenislam.sdk.service.network.response.subscription.PaymentType
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView

internal class PackListAdapter(private val paymentTypes: List<PaymentType>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var selectedIndex = 0
    private val callback  = CallBackProvider.get<SubscriptionCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subscription, parent, false)
        )

    override fun getItemCount(): Int = paymentTypes.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        val planCard:MaterialCardView = itemView.findViewById(R.id.planCard)
        val title: AppCompatTextView = itemView.findViewById(R.id.title)
        val subText: AppCompatTextView = itemView.findViewById(R.id.subText)
        val icTick: AppCompatImageView = itemView.findViewById(R.id.icTick)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = paymentTypes[position]

            title.text = getdata.packageTitle
            subText.text = getdata.packageDescription

            if(absoluteAdapterPosition == selectedIndex) {

                icTick.show()

                itemView.context?.let {
                    planCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            it,
                            R.color.deen_primary
                        )
                    )
                    title.setTextColor(ContextCompat.getColor(it, R.color.deen_white))
                    subText.setTextColor(ContextCompat.getColor(it, R.color.deen_white))
                }

            }
            else{

                icTick.hide()
                itemView.context?.let {
                    planCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            it,
                            R.color.deen_white
                        )
                    )
                    title.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black_deep))
                    subText.setTextColor(ContextCompat.getColor(it, R.color.deen_txt_black_deep))
                }
            }

            itemView.setOnClickListener {
                if(selectedIndex == absoluteAdapterPosition)
                    return@setOnClickListener

                val prevPos = selectedIndex
                selectedIndex = absoluteAdapterPosition
                notifyItemChanged(absoluteAdapterPosition)
                notifyItemChanged(prevPos)
                callback?.selectedPack(getdata)
            }


        }
    }
}