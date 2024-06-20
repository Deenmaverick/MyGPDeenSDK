package com.deenislamic.sdk.views.adapters.tasbeeh;

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class TasbeehDuaAdapter(
    private val callback: tasbeehDuaCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var selectedPos:Int = -1

    private val duaList:ArrayList<Tasbeeh> = arrayListOf(

        Tasbeeh(id=1,dua="Subhan Allah", dua_bn = "সুবহান আল্লাহ", dua_arb = "سُبْحَانَ ٱللَّ"),
        Tasbeeh(id=2,dua="Alhamdulillah", dua_bn = "আলহামদুলিল্লাহ", dua_arb = "ٱلْحَمْدُ لِلَّهِ"),
        Tasbeeh(id=3,dua="Bismillah", dua_bn = "বিসমিল্লাহ", dua_arb = "بِسْمِ اللهِ"),
        Tasbeeh(id=4,dua="Allahu Akbar", dua_bn = "আল্লাহু আকবার", dua_arb = "الله أكبر"),
        Tasbeeh(id=5,dua="Astagfirullah", dua_bn = "আস্তাগফিরুল্লাহ", dua_arb = "أَسْتَغْفِرُ اللّٰهَ"),
        Tasbeeh(id=6,dua="Allah", dua_bn = "আল্লাহ", dua_arb = "الله"),
        Tasbeeh(id=7,dua="La Ilaha illa Allah", dua_bn = "লা ইলাহা ইল্লাল্লাহ", dua_arb = "لَا إِلَٰهَ إِلَّا ٱللَّ"),
        Tasbeeh(id=8,dua="Subhanallahi Wa-Bihamdihi", dua_bn = "সুবহানাল্লাহি ওয়া-বিহামদিহি", dua_arb = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ")
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tasbeeh_dua, parent, false)
        )

    override fun getItemCount(): Int = duaList.size

    fun update(pos:Int)
    {
        selectedPos = pos
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private var item_bg:ConstraintLayout = itemView.findViewById(R.id.item_bg)
        private var dhikrCount:MaterialButton = itemView.findViewById(R.id.dhikrCount)
        private var arabicName:AppCompatTextView = itemView.findViewById(R.id.arabicName)
        private var name:AppCompatTextView = itemView.findViewById(R.id.name)

        override fun onBind(position: Int) {
            super.onBind(position)


            if(selectedPos==position)
            {

                Log.e("RecyclerViewDebug", "Styling position $position as selected")

                itemView.isEnabled = false
                item_bg.setBackgroundResource(R.drawable.tasbeeh_dua_gradiant)
                dhikrCount.setTextColor(ContextCompat.getColor(name.context,R.color.deen_white))
                dhikrCount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(name.context,R.color.deen_border))
                arabicName.setTextColor(ContextCompat.getColor(name.context,R.color.deen_white))
                name.setTextColor(ContextCompat.getColor(name.context,R.color.deen_white_70))
            }
            else
            {
                itemView.isEnabled = true
                item_bg.background = null
                dhikrCount.setTextColor(ContextCompat.getColor(name.context,R.color.deen_card_checkbox_bg))
                dhikrCount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(name.context,R.color.deen_card_bg))
                arabicName.setTextColor(ContextCompat.getColor(name.context,R.color.deen_txt_black_deep))
                name.setTextColor(ContextCompat.getColor(name.context,R.color.deen_txt_ash))
            }

            dhikrCount.text = dhikrCount.context.getString(R.string.tasbeeh_dhikir_count,(position+1).toString().numberLocale())
            arabicName.text = duaList[position].dua_arb

            if(DeenSDKCore.GetDeenLanguage() == "en")
            name.text = duaList[position].dua
            else
                name.text = duaList[position].dua_bn


                itemView.setOnClickListener {
                    if(selectedPos!=position) {
                        selectedPos = position
                        callback.selectedDua(position)
                    }
                }

            if (position == 0) {
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 4.dp
            }
            else if (position == itemCount-1) {
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 16.dp
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 4.dp
            }
            else
            {
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 4.dp
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 4.dp
            }


        }
    }
}

internal interface tasbeehDuaCallback
{
    fun selectedDua(duaid:Int)
}
