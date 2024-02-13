package com.deenislam.sdk.views.adapters.islamimasail;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.IslamiMasailCallback
import com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder

internal class MasailQuestionListAdapter(private val customViewtype:Int = 0) : RecyclerView.Adapter<BaseViewHolder>() {

    // View types
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_RECENT_ITEM = 2
    private val VIEW_TYPE_LOADING = 1
    private var isBottomLoading = false

    private var questiondatalist: ArrayList<Data> = arrayListOf()

    private var callback = CallBackProvider.get<IslamiMasailCallback>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_ITEM -> R.layout.item_masail_card
            VIEW_TYPE_LOADING -> R.layout.item_bottom_loading
            VIEW_TYPE_RECENT_ITEM -> R.layout.item_masail_viewed_question
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater
            .from(parent.context.getLocalContext())
            .inflate(layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < questiondatalist.size) {
            if (customViewtype == VIEW_TYPE_ITEM)
                VIEW_TYPE_ITEM
            else customViewtype
        }
        else VIEW_TYPE_LOADING
    }

    override fun getItemCount(): Int = questiondatalist.size + if(isBottomLoading) 1 else 0


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    fun update(data: List<Data>) {
        val newData = ArrayList(questiondatalist)
        newData.addAll(data)

        // Remove duplicates using a HashSet
        val uniqueData = newData.distinctBy { it.Id } // assuming 'id' is a unique identifier

        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                questiondatalist,
                uniqueData
            )
        )
        questiondatalist = ArrayList(uniqueData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateFav(newdata: Data)
    {

        val oldList = questiondatalist.toList()

        val matchingVideoDataIndex = oldList.indexOfFirst { it.Id == newdata.Id }

        if(matchingVideoDataIndex!=-1) {
            questiondatalist[matchingVideoDataIndex] = newdata
        }


        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                oldList,
                questiondatalist
            )
        )

        diffResult.dispatchUpdatesTo(this)
    }

    fun delData(copy: Data) {

        val oldList = questiondatalist.toList()

        val matchingVideoDataIndex = oldList.indexOfFirst { it.Id == copy.Id }

        if(matchingVideoDataIndex!=-1) {
            questiondatalist.removeAt(matchingVideoDataIndex)
        }


        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                oldList,
                questiondatalist
            )
        )

        diffResult.dispatchUpdatesTo(this)
    }

    fun setLoading(loading: Boolean) {
        if (isBottomLoading != loading) {
            isBottomLoading = loading
            if (loading) {
                // Show loading indicator
                notifyItemInserted(questiondatalist.size)
            } else {
                // Hide loading indicator
                notifyItemRemoved(questiondatalist.size)
            }
        }
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val quesNo:AppCompatTextView by lazy { itemView.findViewById(R.id.quesNo) }
        private val username:AppCompatTextView by lazy { itemView.findViewById(R.id.username) }
        private val readCount:AppCompatTextView by lazy { itemView.findViewById(R.id.readCount) }
        private val question:AppCompatTextView by lazy { itemView.findViewById(R.id.question) }
        private val bookmarkTxt:AppCompatTextView by lazy { itemView.findViewById(R.id.bookmarkTxt) }
        private val boomarkLayout:ConstraintLayout by lazy { itemView.findViewById(R.id.boomarkLayout) }
        private val shareTxt:AppCompatTextView by lazy { itemView.findViewById(R.id.shareTxt) }
        private val shareLayout:ConstraintLayout by lazy { itemView.findViewById(R.id.shareLayout) }
        private val icBookMark:AppCompatImageView by lazy { itemView.findViewById(R.id.icBookMark) }


        //recent read
        private val title:AppCompatTextView by lazy { itemView.findViewById(R.id.title) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype){
                VIEW_TYPE_ITEM -> {

                    val getdata = questiondatalist[position]

                    quesNo.text = getdata.Id.toString().numberLocale()
                    username.text = getdata.QRaiserName
                    readCount.text = itemView.context.getString(R.string.masailquesReadCount,getdata.viewCount.toString().numberLocale())
                    question.text = getdata.title
                    bookmarkTxt.text = itemView.context.getString(R.string.masailquesFavCount,getdata.favCount.toString().numberLocale())
                    boomarkLayout.setOnClickListener {
                        callback = CallBackProvider.get<IslamiMasailCallback>()
                        callback?.questionBookmarkClicked(getdata)
                    }
                    shareTxt.text = itemView.context.getString(R.string.masailquesShareCount,getdata.favCount.toString().numberLocale())
                    shareLayout.setOnClickListener {
                        callback = CallBackProvider.get<IslamiMasailCallback>()
                        callback?.questionShareClicked(getdata)
                    }

                    if(getdata.IsFavorite)
                        icBookMark.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.deen_ic_bookmark_active))
                    else
                        icBookMark.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.deen_ic_bookmark))

                    itemView.setOnClickListener {
                        callback = CallBackProvider.get<IslamiMasailCallback>()
                        Log.e("qcallback",callback.toString())
                        callback?.masailQuestionClicked(getdata)
                    }

                }

                VIEW_TYPE_RECENT_ITEM -> {

                    val getdata = questiondatalist[position]

                    title.text = getdata.title
                    itemView.setOnClickListener {
                        callback = CallBackProvider.get<IslamiMasailCallback>()
                        callback?.masailQuestionClicked(getdata)
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