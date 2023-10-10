package com.deenislam.sdk.views.adapters.quran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseViewHolder


internal class MyQuranAdapter(
    private val myQuranCallback: MyQuranCallback
) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_my_quran, parent, false)
        )

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val menuIcon: AppCompatImageView = itemView.findViewById(R.id.ic_fav)
        private val menuName: AppCompatTextView = itemView.findViewById(R.id.menuName)

        override fun onBind(position: Int) {
            super.onBind(position)

            itemView.setOnClickListener {
                myQuranCallback.menuClicked(position)
            }

            when(position)
            {
                0->
                {
                    menuIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_favorite))
                    menuName.text = "Favorites"
                }

                1->
                {
                    menuIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_checked_circle))
                    menuName.text = "Downloads"
                }

                2->
                {
                    //menuIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_local_library))
                    menuName.text = "My Playlist"
                }

                3->
                {
                    menuIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_quran_menu))
                    menuName.text = "Daily Verse"
                }

                4->
                {
                    menuIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_dua))
                    menuName.text = "Quranic Dua"
                }
            }

        }
    }
}

internal interface MyQuranCallback
{
    fun menuClicked(position: Int)
}