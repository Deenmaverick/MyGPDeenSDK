package com.deenislamic.sdk.views.adapters.subscription;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.SubscriptionCallback
import com.deenislamic.sdk.service.network.response.subscription.PaymentType
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.GP
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson

internal class PackListAdapter(private var paymentTypes: List<PaymentType>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val backupList = paymentTypes
    private var selectedMethod = ""
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

    fun updatePaymentMethod(method: String, subscriptionStatus: String): List<PaymentType> {

        when(method){
            "bkash" -> {
                paymentTypes = backupList.filter { it.isBkashEnable }
            }
            GP -> {
                paymentTypes = backupList.filter { it.isGPEnable }
            }


            else -> {
                paymentTypes = arrayListOf()
            }
        }

        if(subscriptionStatus == "1bknon")
            paymentTypes = paymentTypes.filter { it.isRecurring }

        Log.e("updatePaymentMethod","$subscriptionStatus $method ${Gson().toJson(paymentTypes)}")

        notifyDataSetChanged()


        return paymentTypes

    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val planCard:MaterialCardView = itemView.findViewById(R.id.planCard)
        private val title: AppCompatTextView = itemView.findViewById(R.id.title)
        private val subText: AppCompatTextView = itemView.findViewById(R.id.subText)
        private val amount:AppCompatTextView = itemView.findViewById(R.id.amount)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = paymentTypes[position]

            title.text = getdata.packageTitle
            subText.text = getdata.packageDescription
            amount.text = "৳${getdata.packageAmount.numberLocale()}"


            itemView.setOnClickListener {
                callback?.selectedPack(getdata)
            }


        }
    }
}