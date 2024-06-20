package com.deenislamic.sdk.views.adapters.hadith;

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.hadith.preview.Data
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.stripHtml
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

const val HEADER:Int = 1
const val CONTENT:Int = 2
internal class HadithPreviewAdapter(
    private val callback: HadithPreviewCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var haditDataList: ArrayList<Data> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =

        when(viewType)
        {
            HEADER -> ViewHolder(
                LayoutInflater.from(parent.context.getLocalContext())
                    .inflate(R.layout.item_hadith_preview_header, parent, false)
            )

            CONTENT -> ViewHolder(
                LayoutInflater.from(parent.context.getLocalContext())
                    .inflate(R.layout.item_hadith_preview, parent, false)
            )

            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }


    fun update(data: List<Data>)
    {
        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                haditDataList,
                data
            )
        )
        haditDataList = data as ArrayList<Data>
        diffResult.dispatchUpdatesTo(this)
    }

    fun update(position: Int, fav: Boolean)
    {
        haditDataList[position].IsFavorite = fav
        notifyItemChanged(position)
    }

    fun getDataSize() = haditDataList.size-1


    override fun getItemCount(): Int = haditDataList.size

    override fun getItemViewType(position: Int): Int {
      /*  return if(position == 0) HEADER
        else */ return CONTENT
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }


    internal inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val duaCat:MaterialButton = itemView.findViewById(R.id.duaCat)
        private val duaSub:AppCompatTextView = itemView.findViewById(R.id.duaSub)
        private val arabicName:AppCompatTextView = itemView.findViewById(R.id.arabicName)
        private val favBtn:MaterialButton = itemView.findViewById(R.id.favBtn)

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                CONTENT ->
                {
                    val data = haditDataList[position]

                    duaCat.text = duaCat.context.getString(R.string.hadith_preview_pos,data.HadithNumber.numberLocale())

                    var duaSubData = data.HadithText.stripHtml()
                    var arabicNameData = data.HadithArabicText.stripHtml()

                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        duaSubData = Html.fromHtml(
                            data.HadithText,
                            Html.FROM_HTML_MODE_LEGACY
                        ).toString()

                        arabicNameData = Html.fromHtml(
                            data.HadithArabicText,
                            Html.FROM_HTML_MODE_LEGACY
                        ).toString()

                    } else {
                        @Suppress("DEPRECATION")
                        duaSubData = Html.fromHtml(
                            data.HadithText
                        ).toString()
                        arabicNameData = Html.fromHtml(
                            data.HadithArabicText
                        ).toString()
                    }*/

                    duaSubData = "<html><body style='text-align:justify;'>$duaSubData</body></html>"

                    arabicNameData = "<html><body style='text-align:justify;'>$arabicNameData</body></html>"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


                        duaSub.text = Html.fromHtml(
                            duaSubData,
                            Html.FROM_HTML_MODE_LEGACY
                        )

                        arabicName.text = Html.fromHtml(
                            arabicNameData,
                            Html.FROM_HTML_MODE_LEGACY
                        )

                    } else {
                        @Suppress("DEPRECATION")
                        duaSub.text = Html.fromHtml(
                            duaSubData
                        )

                        arabicName.text = Html.fromHtml(
                            arabicNameData
                        )
                    }


                    if(data.IsFavorite)
                    {
                        favBtn.setIconResource(R.drawable.ic_favorite_primary_active)
                        favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.deen_brand_favorite)
                        favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.deen_txt_black_deep))

                    }
                    else
                    {
                        favBtn.setIconResource(R.drawable.ic_favorite)
                        favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.deen_txt_black_deep)
                        favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.deen_txt_black_deep))

                    }

                    favBtn.setOnClickListener {

                        callback.favcClick(data.IsFavorite,data.Id,position)
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
internal interface HadithPreviewCallback
{
    fun favcClick(isFavorite: Boolean, duaId: Int, position: Int)
}