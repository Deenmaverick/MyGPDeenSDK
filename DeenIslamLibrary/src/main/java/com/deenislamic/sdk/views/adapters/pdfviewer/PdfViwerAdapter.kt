package com.deenislamic.sdk.views.adapters.pdfviewer;

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.PdfPageQuality
import com.deenislamic.sdk.views.base.BaseViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class PdfViwerAdapter(private val pdfRenderer: PdfRenderer) : RecyclerView.Adapter<BaseViewHolder>() {

    private val mutex = Mutex()
    private val jobs = mutableMapOf<Int, Job>() // Track jobs for each position



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pdf_viewer, parent, false)
        )

    override fun getItemCount(): Int = pdfRenderer.pageCount

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        val position = holder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            // Cancel the job for this position when it is no longer visible
            jobs[position]?.cancel()
            jobs.remove(position)
        }
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val photo: AppCompatImageView = itemView.findViewById(R.id.page)
        override fun onBind(position: Int) {
            super.onBind(position)

            /*val page = pdfRenderer.openPage(position)

            val quality = PdfPageQuality.QUALITY_1440

            val width = quality.value
            val height = quality.value * page.height / page.width

           val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            photo.setImageBitmap(bitmap)
            page.close()*/

            // Cancel the previous job if any
            jobs[position]?.cancel()

            // Start a new job for the position
            val job = CoroutineScope(Dispatchers.Main).launch {
                val bitmap = renderPdfPage(position)
                if (bitmap != null) {
                    photo.setImageBitmap(bitmap)
                }
            }
            jobs[position] = job

        }

        private suspend fun renderPdfPage(position: Int): Bitmap? {
            return withContext(Dispatchers.IO) {
                mutex.withLock {
                    var page: PdfRenderer.Page? = null
                    try {
                        page = pdfRenderer.openPage(position)
                        val quality = PdfPageQuality.QUALITY_1440
                        val width = quality.value
                        val height = quality.value * page.height / page.width
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    } finally {
                        page?.close()
                    }
                }
            }
        }
    }
}