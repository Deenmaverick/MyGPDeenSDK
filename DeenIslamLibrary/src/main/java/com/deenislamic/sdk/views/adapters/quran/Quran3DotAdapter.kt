package com.deenislamic.sdk.views.adapters.quran;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.AlQuranAyatCallback
import com.deenislamic.sdk.service.models.common.OptionList
import com.deenislamic.sdk.service.network.response.quran.qurangm.ayat.Ayath
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class Quran3DotAdapter(
    private val optionList3Dot: ArrayList<OptionList>,
    private val ayathData: Ayath ? = null) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<AlQuranAyatCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_option_list, parent, false)
        )

    override fun getItemCount(): Int = optionList3Dot.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val ic_fav:AppCompatImageView = itemView.findViewById(R.id.ic_fav)
        private val menuName:AppCompatTextView = itemView.findViewById(R.id.menuName)
        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = optionList3Dot[position]

            ic_fav.setImageDrawable(ContextCompat.getDrawable(itemView.context,getdata.icon))
            menuName.text = getdata.title

            itemView.setOnClickListener {
                callback?.option3dotClicked(getdata,ayathData)
            }

        }
    }
}