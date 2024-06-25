package com.deenislamic.sdk.views.adapters.share;

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.ShareCallback
import com.deenislamic.sdk.service.network.response.share.Data
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class BackgroundAdapter(private val data: List<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<ShareCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_wallpaper, parent, false)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val bgWallpaper:AppCompatImageView = itemView.findViewById(R.id.bgWallpaper)
        private val shareHint:AppCompatTextView = itemView.findViewById(R.id.shareHint)

        override fun onBind(position: Int) {
            super.onBind(position)

            var getData:Data? = null

            if(absoluteAdapterPosition==0) {

                bgWallpaper.load(R.drawable.deen_bg_wallpaper_main)
                shareHint.show()
            }
            else{
                getData = data[absoluteAdapterPosition]
                bgWallpaper.imageLoad(url = BASE_CONTENT_URL_SGP+getData.imageurl, placeholder_1_1 = true, customMemoryKey = "custom_share_bg_${getData.Id}")
                shareHint.hide()
            }

            itemView.setOnClickListener {

                val drawable = bgWallpaper.drawable
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    // Now you can use the 'bitmap' as needed
                    callback?.selectedWallpaper(absoluteAdapterPosition,bitmap,getData)
                } else {
                    // Handle the case where bgWallpaper is not a BitmapDrawable
                    callback?.selectedWallpaper(absoluteAdapterPosition,null,getData)
                }


            }


        }
    }
}