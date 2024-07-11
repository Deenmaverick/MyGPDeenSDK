package com.deenislamic.sdk.views.adapters.islamicbook

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.islamicbook.BookDownloadPayload
import com.deenislamic.sdk.service.network.response.islamicbook.Data
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.io.File

internal class IslamicBookItemAdapter(
    private val callback: IslamicBookItemCallback
) : RecyclerView.Adapter<BaseViewHolder>()  {

    private val bookList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_book, parent, false)
        )

    fun update(data: List<Data>)
    {
        bookList.clear()
        bookList.addAll(data)
        notifyDataSetChanged()
    }

    fun findPositionByData(id: String?): Int {
        for ((index, item) in bookList.withIndex()) {
            // Replace the condition with your matching criteria
            if ("${item.Id}.pdf" == id) {
                return index
            }
        }
        return RecyclerView.NO_POSITION // Item not found
    }

    override fun getItemCount(): Int  = bookList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        holder.onBind(position)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        if (payloads.isNotEmpty() && payloads[payloads.size-1] is BookDownloadPayload) {
            val unpackpayload = payloads[payloads.size-1] as BookDownloadPayload
            if (holder is ViewHolder) {
                // Handle the custom payload in the inner ViewHolder
                holder.updateDownloadProgress(unpackpayload)
            } else {
                // Call the updateDownloadProgress method in the base ViewHolder
                super.onBindViewHolder(holder, position, payloads)
            }
        } else {
            // Perform the full binding if no payloads
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private var isCancelSet = false

        private val bookImage: AppCompatImageView = itemView.findViewById(R.id.imgviw_book)
        private val bookName: AppCompatTextView = itemView.findViewById(R.id.txtviw_bookname)
        private val authorName: AppCompatTextView = itemView.findViewById(R.id.txtviw_authorname)

        private val likeButton: MaterialButton = itemView.findViewById(R.id.btn_like)
        private val downloadButton: MaterialButton = itemView.findViewById(R.id.btn_download)

        private val downloadLayout: ConstraintLayout = itemView.findViewById(R.id.constraintlay_download)
        private val downloadProgressBar: LinearProgressIndicator = itemView.findViewById(R.id.progressbar_download_progress)
        private val downloadProgress: AppCompatTextView = itemView.findViewById(R.id.txtviw_download_progress)
        private val downloadCancel: MaterialButton = itemView.findViewById(R.id.btn_cancel_download)

        override fun onBind(position: Int) {
            super.onBind(position)

            val book = bookList[absoluteAdapterPosition]

            bookImage.imageLoad(url = BASE_CONTENT_URL_SGP + book.imageurl1, placeholder_1_1 = true)
            bookName.text = book.title
            authorName.text = book.authorName

            if (book.IsFavorite == true){
                likeButton.setIconResource(R.drawable.ic_favorite_primary_active)
            } else if (book.IsFavorite == false){
                likeButton.setIconResource(R.drawable.ic_fav_quran)
            }

            itemView.setOnClickListener {
                callback.bookItemClieked(book.Id, book.contenturl, book.title)
            }

            likeButton.setOnClickListener {

                if (book.IsFavorite == true){
                    likeButton.setIconResource(R.drawable.ic_fav_quran)
                    book.IsFavorite = false
                } else if (book.IsFavorite == false){
                    likeButton.setIconResource(R.drawable.ic_favorite_primary_active)
                    book.IsFavorite = true
                }
                //notifyItemChanged(position)
                callback.bookLikeClicked(book.Id, book.categoryId, book.title, book.IsFavorite, position)
            }

            downloadButton.setOnClickListener {

                val drawable = bookImage.drawable
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    // Now you can use the 'bitmap' as needed
                    callback.bookDownloadClickd(book.contenturl, book.Id, book.title, book.authorName, bitmap)
                } else {
                    // Handle the case where bgWallpaper is not a BitmapDrawable
                    callback.bookDownloadClickd(book.contenturl, book.Id, book.title, book.authorName, null)
                }

            }

            val filePath = File(itemView.context.filesDir, "islamicbook/${book.Id}/${book.Id}.pdf")

            if (filePath.exists()) {
                downloadLayout.hide()
                likeButton.show()
                downloadButton.show()
                downloadButton.text = downloadButton.context.getString(R.string.downloaded)
                downloadButton.setIconResource(R.drawable.deen_ic_check)
                downloadButton.isClickable = false

            }

        }

        fun updateDownloadProgress(payloads: BookDownloadPayload) {

            if(payloads.isCancelled){
                downloadLayout.hide()
                likeButton.show()
                downloadButton.show()
                downloadButton.text = downloadButton.context.getString(R.string.download)
                downloadButton.setIconResource(R.drawable.ic_download_quran)
                downloadButton.isClickable = true
                isCancelSet = false
            }
            else if(payloads.isComplete){
                downloadLayout.hide()
                likeButton.show()
                downloadButton.show()
                downloadButton.text = downloadButton.context.getString(R.string.downloaded)
                downloadButton.setIconResource(R.drawable.deen_ic_check)
                downloadButton.isClickable = false
                isCancelSet = false
            }
            else{

                likeButton.hide()
                downloadButton.hide()
                downloadLayout.show()
                downloadProgressBar.progress = payloads.progress
                downloadProgress.text = "${payloads.progress}%".numberLocale()

                if(!isCancelSet) {
                    downloadCancel.setOnClickListener {
                        callback.bookDownloadCancelClicked(payloads.notificationId)
                    }

                    isCancelSet = true
                }
            }

        }
    }
}

interface IslamicBookItemCallback
{
    fun bookItemClieked(Id: Int, contentUrl: String, bookName: String)
    fun bookLikeClicked(Id: Int, categoryId: Int, title: String, isFavorite: Boolean, position: Int)
    fun bookDownloadClickd(
        contentUrl: String,
        id: Int,
        title: String,
        authorName: String,
        bitmap: Bitmap?
    )
    fun bookDownloadCancelClicked(notificationId: Int)
}