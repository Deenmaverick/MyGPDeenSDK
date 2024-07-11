package com.deenislamic.sdk.views.islamicbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.islamicbook.downloadedmodel.DownloadedBook
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator

internal class DownloadedBookAdapter(
    private val callback: DownloadedIslamicBookItemCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val downloadedBookList: ArrayList<DownloadedBook> = arrayListOf()

    fun update(data: List<DownloadedBook>)
    {
        downloadedBookList.clear()
        downloadedBookList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_book, parent, false)
        )

    override fun getItemCount(): Int = downloadedBookList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private var isCancelSet = false

        private val bookImage: AppCompatImageView = itemView.findViewById(R.id.imgviw_book)
        private val bookName: AppCompatTextView = itemView.findViewById(R.id.txtviw_bookname)
        private val authorName: AppCompatTextView = itemView.findViewById(R.id.txtviw_authorname)

        private val likeButton: MaterialButton = itemView.findViewById(R.id.btn_like)
        private val downloadButton: MaterialButton = itemView.findViewById(R.id.btn_download)

        private val downloadLayout: ConstraintLayout =
            itemView.findViewById(R.id.constraintlay_download)
        private val downloadProgressBar: LinearProgressIndicator =
            itemView.findViewById(R.id.progressbar_download_progress)
        private val downloadProgress: AppCompatTextView =
            itemView.findViewById(R.id.txtviw_download_progress)
        private val downloadCancel: MaterialButton = itemView.findViewById(R.id.btn_cancel_download)

        override fun onBind(position: Int) {
            super.onBind(position)

            val book = downloadedBookList[absoluteAdapterPosition]

            likeButton.visibility = View.GONE
            downloadButton.text = downloadButton.context.getString(R.string.delete)
            downloadButton.setIconResource(R.drawable.ic_delete)

            bookImage.setImageBitmap(book.bookImage)
            bookName.text = book.bookName
            authorName.text = book.bookAuthorName

            downloadButton.setOnClickListener {
                callback.deleteBookClieked(position)
            }

            itemView.setOnClickListener {
                callback.bookItemClicked(position)
            }
        }

    }
}

interface DownloadedIslamicBookItemCallback
{
    fun deleteBookClieked(position: Int)
    fun bookItemClicked(position: Int)
}