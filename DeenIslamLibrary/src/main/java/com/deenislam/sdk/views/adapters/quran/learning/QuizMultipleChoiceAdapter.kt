package com.deenislam.sdk.views.adapters.quran.learning;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.QuranLearningCallback
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.quiz.Option
import com.google.android.material.card.MaterialCardView

internal class QuizMultipleChoiceAdapter(private val options: ArrayList<Option>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<QuranLearningCallback>()
    private var lastUpdatedItemID = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quiz_multiple_choice, parent, false)
        )

    override fun getItemCount(): Int = options.size

    fun updateData(newdata: Option)
    {
        val oldList = options.toList()

        val matchingVideoDataIndex = oldList.indexOfFirst { it.OptionId == newdata.OptionId }

        if(lastUpdatedItemID!=-1)
            options[lastUpdatedItemID] = options[lastUpdatedItemID].copy(selected = !options[lastUpdatedItemID].selected)

        if(matchingVideoDataIndex!=-1) {
            lastUpdatedItemID = matchingVideoDataIndex
            options[matchingVideoDataIndex] = newdata

        }

        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                oldList,
                options
            )
        )

        diffResult.dispatchUpdatesTo(this)

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val answerCard:MaterialCardView = itemView.findViewById(R.id.answerCard)
        private val count:AppCompatTextView = itemView.findViewById(R.id.count)
        private val answer:AppCompatTextView = itemView.findViewById(R.id.answer)
        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = options[position]

            count.text = "${(position+1)}.".numberLocale()
            answer.text = getData.Description

            if(getData.selected) {
                Log.e("QUIZselected","OK")
                answerCard.strokeWidth = 1.dp
            }
            else
                answerCard.strokeWidth = 0


            itemView.setOnClickListener {
                callback?.courseQuizAnswerSelected(getData)
            }

        }
    }

    internal class DataDiffCallback(
        private val oldList: List<Option>,
        private val newList: ArrayList<Option>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].OptionId == newList[newItemPosition].OptionId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}