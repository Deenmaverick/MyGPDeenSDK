package com.deenislamic.sdk.views.adapters.pdfviewer;

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.libs.photoview.PhotoView
import com.deenislamic.sdk.utils.PdfPageQuality
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class PdfViwerAdapter(private val pdfRenderer: PdfRenderer) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pdf_viewer, parent, false)
        )

    override fun getItemCount(): Int = pdfRenderer.pageCount

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val photo: AppCompatImageView = itemView.findViewById(R.id.page)
        override fun onBind(position: Int) {
            super.onBind(position)

            val page = pdfRenderer.openPage(position)

            val quality = PdfPageQuality.QUALITY_1440

            val width = quality.value
            val height = quality.value * page.height / page.width

           val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            photo.setImageBitmap(bitmap)
            page.close()

        }
    }
}