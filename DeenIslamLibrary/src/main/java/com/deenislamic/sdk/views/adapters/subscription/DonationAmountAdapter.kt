package com.deenislamic.sdk.views.adapters.subscription;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.SubscriptionCallback
import com.deenislamic.sdk.service.models.subscription.DonateAmount
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView

internal class DonationAmountAdapter(private val donateAmountList: ArrayList<DonateAmount>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val backupData = ArrayList(donateAmountList)
    private val callback = CallBackProvider.get<SubscriptionCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_donation_amount_card, parent, false)
        )

    override fun getItemCount(): Int = donateAmountList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun getdata() = donateAmountList
    fun update(newdata: DonateAmount)
    {

        val copyOldData = ArrayList(backupData)
        val matchingVideoDataIndex = copyOldData.indexOfFirst { it.amount == newdata.amount }

        if(matchingVideoDataIndex!=-1) {
            copyOldData[matchingVideoDataIndex] = newdata
        }


        /*val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                donateAmountList,
                copyOldData
            )
        )*/

        donateAmountList.clear() // Clear the old data
        donateAmountList.addAll(copyOldData) // Update with the new data
        notifyDataSetChanged()
        /*diffResult.dispatchUpdatesTo(this)*/
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val donateAmountCard:MaterialCardView = itemView.findViewById(R.id.donateAmountCard)
        private val amountTxt:AppCompatTextView = itemView.findViewById(R.id.amountTxt)
        private val currencyTxt:AppCompatTextView = itemView.findViewById(R.id.currencyTxt)
        override fun onBind(position: Int) {
            super.onBind(position)
            val getdata = donateAmountList[position]
            amountTxt.text = getdata.amount.toString().numberLocale()
            currencyTxt.text = itemView.context.getString(R.string.taka)
            if(getdata.isActive){
                donateAmountCard.strokeWidth = 1.dp
                itemView.context.let {
                    amountTxt.setTextColor(ContextCompat.getColor(it,R.color.deen_primary))
                    currencyTxt.setTextColor(ContextCompat.getColor(it,R.color.deen_primary))
                }

            }else{
                donateAmountCard.strokeWidth = 0
                itemView.context.let {
                    amountTxt.setTextColor(ContextCompat.getColor(it,R.color.deen_txt_black_deep))
                    currencyTxt.setTextColor(ContextCompat.getColor(it,R.color.deen_txt_ash))
                }
            }
            itemView.setOnClickListener {
                callback?.selectedCustomDonation(getdata)
            }

        }
    }

    internal class DataDiffCallback(
        private val oldList: ArrayList<DonateAmount>,
        private val newList: ArrayList<DonateAmount>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].isActive == newList[newItemPosition].isActive
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
}