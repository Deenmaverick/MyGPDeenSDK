package com.deenislam.sdk.views.adapters.podcast;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.PodcastCallback
import com.deenislam.sdk.service.network.response.podcast.comment.Comment
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder

internal class LivePodcastChatAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<PodcastCallback>()

    private var comments: ArrayList<Comment> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_podcast_chat, parent, false)
        )

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun update(comments: List<Comment>) {
        val newData = ArrayList(this.comments)
        newData.addAll(comments)

        // Remove duplicates using a HashSet
        val uniqueData = newData.distinctBy { it.Id } // assuming 'id' is a unique identifier

        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                this.comments,
                uniqueData
            )
        )
        this.comments = ArrayList(uniqueData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateFav(newdata: Comment)
    {

        val oldList = comments.toList()

        val matchingVideoDataIndex = oldList.indexOfFirst { it.Id == newdata.Id }

        if(matchingVideoDataIndex!=-1) {
            comments[matchingVideoDataIndex] = newdata
        }


        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                oldList,
                comments
            )
        )

        diffResult.dispatchUpdatesTo(this)
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val ic_avatar:AppCompatImageView = itemView.findViewById(R.id.ic_avatar)
        private val user_name:AppCompatTextView = itemView.findViewById(R.id.user_name)
        private val comment_time:AppCompatTextView = itemView.findViewById(R.id.comment_time)
        private val user_comment:AppCompatTextView = itemView.findViewById(R.id.user_comment)
        private val like_count:AppCompatTextView = itemView.findViewById(R.id.like_count)
        private val likeLayout:LinearLayout = itemView.findViewById(R.id.likeLayout)
        private val ic_like:AppCompatImageView = itemView.findViewById(R.id.ic_like)
        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = comments[absoluteAdapterPosition]

            val profileUrl = if(getdata.isSocial) getdata.UImage else BASE_CONTENT_URL_SGP+getdata.UImage
            ic_avatar.imageLoad(
                url = profileUrl,
                placeholder_1_1 =true,
                custom_placeholder_1_1 = R.drawable.ic_avatar)

            user_name.text = getdata.UName
            comment_time.text = getdata.CTime
            user_comment.text = getdata.Text
            like_count.text = itemView.context.getString(R.string.like_count,getdata.LikeCount.toString().numberLocale())

            if(getdata.isLiked){
                ic_like.setColorFilter(ContextCompat.getColor(itemView.context,R.color.deen_primary))
                like_count.setTextColor(ContextCompat.getColor(itemView.context,R.color.deen_primary))
            }
            else{
                ic_like.setColorFilter(ContextCompat.getColor(itemView.context,R.color.deen_txt_black_deep))
                like_count.setTextColor(ContextCompat.getColor(itemView.context,R.color.deen_txt_black_deep))
            }

            likeLayout.setOnClickListener {
                callback?.livepodcastFavClicked(getdata)
            }
        }
    }

    internal class DataDiffCallback(
        private val oldList: List<Comment>,
        private val newList: List<Comment>
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