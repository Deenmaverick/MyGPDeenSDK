package com.deenislamic.sdk.views.islamicbook.adapter

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.islamicbook.BookDownloadPayload
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.islamicbook.callback.IslamicBookHomeItemCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.io.File

internal class IslamicBookHomeItemAdapter(
    private val bookList: List<Item>
) : RecyclerView.Adapter<BaseViewHolder>()   {

    private var callback = CallBackProvider.get<IslamicBookHomeItemCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_book, parent, false)
        )

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

            Log.d("TheBookDataDataData", "data:"+book.toString())

            bookImage.imageLoad(url = BASE_CONTENT_URL_SGP + book.imageurl1, placeholder_1_1 = true)
            bookName.text = book.Title
            if (book.MText != null){
                authorName.text = book.MText
            } else if (book.MText == null){
                authorName.visibility = View.INVISIBLE
            }


            itemView.setOnClickListener {
                callback = CallBackProvider.get<IslamicBookHomeItemCallback>()
                callback?.bookItemClieked(book.Id, book.Reference, book.Title)
            }

            likeButton.visibility = View.GONE

            likeButton.setOnClickListener {
                /*
                if (book.SurahId == 0){
                    likeButton.setIconResource(R.drawable.ic_favorite_primary_active)
                    book.SurahId = 1
                } else if (book.SurahId == 1){
                    likeButton.setIconResource(R.drawable.ic_fav_quran)
                    book.SurahId = 0
                }
                */

                callback = CallBackProvider.get<IslamicBookHomeItemCallback>()
                callback?.bookLikeClicked(book.Id, book.Id, book.Title, book.FeatureName)
            }

            downloadButton.setOnClickListener {
                isCancelSet = false
                callback = CallBackProvider.get<IslamicBookHomeItemCallback>()


                val drawable = bookImage.drawable
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    // Now you can use the 'bitmap' as needed
                    Log.d("IslamicBookHomeItem", "if"+callback.toString())
                    callback?.bookDownloadClickd(book.Reference, book.Id, book.Title, book.MText, bitmap)
                } else {
                    // Handle the case where bgWallpaper is not a BitmapDrawable
                    Log.d("IslamicBookHomeItem", "else"+callback.toString())
                    callback?.bookDownloadClickd(book.Reference, book.Id, book.Title, book.MText, null)
                }
            }

            val filePath = File(itemView.context.filesDir, "islamicbook/${book.Id}/${book.Id}.pdf")

            if (filePath.exists()) {
                downloadLayout.hide()
                likeButton.hide()
                downloadButton.show()
                downloadButton.text = downloadButton.context.getString(R.string.downloaded)
                downloadButton.setIconResource(R.drawable.deen_ic_check)
                downloadButton.isClickable = false

            }
        }

        fun updateDownloadProgress(payloads: BookDownloadPayload) {

            if(payloads.isCancelled){
                downloadLayout.hide()
                likeButton.hide()
                downloadButton.show()
                downloadButton.text = downloadButton.context.getString(R.string.download)
                downloadButton.setIconResource(R.drawable.ic_download_quran)
                downloadButton.isClickable = true
                isCancelSet = false
            }
            else if(payloads.isComplete){
                downloadLayout.hide()
                likeButton.hide()
                downloadButton.show()
                downloadButton.text = downloadButton.context.getString(R.string.downloaded)
                downloadButton.setIconResource(R.drawable.deen_ic_check)
                downloadButton.isClickable = false
                isCancelSet = false
            }
            else{

                likeButton.hide()
                downloadButton.hide()
                if(!downloadLayout.isShown)
                downloadLayout.show()
                downloadProgressBar.progress = payloads.progress
                downloadProgress.text = "${payloads.progress}%".numberLocale()

                if(!isCancelSet) {
                    downloadCancel.setOnClickListener {
                        callback = CallBackProvider.get<IslamicBookHomeItemCallback>()
                        callback?.bookDownloadCancelClicked(payloads.notificationId)
                    }

                    isCancelSet = true
                }
            }

        }
    }
}