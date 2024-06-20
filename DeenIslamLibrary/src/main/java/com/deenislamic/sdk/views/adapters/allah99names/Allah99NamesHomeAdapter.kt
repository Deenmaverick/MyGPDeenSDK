package com.deenislamic.sdk.views.adapters.allah99names;

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.Allah99NameCallback
import com.deenislamic.sdk.service.network.response.allah99name.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.invisible
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.progressindicator.CircularProgressIndicator

private const val MAINCARD = 0
private const val EMPTYCARD = 1
internal class Allah99NamesHomeAdapter(
    private val isShowHeadSerial: Boolean = true,
    nameCallback: Allah99NameCallback? = null
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val itemList: ArrayList<Data> = arrayListOf()
    private val callback =
        nameCallback ?: CallBackProvider.get<Allah99NameCallback>()

    private val allah99NameCallback = CallBackProvider.get<Allah99NameCallback>()

    private val colorArray:ArrayList<String> = arrayListOf()

    private var isPlaying = false
    private var isNext = false
    private var activePosition = -1  // -1 indicates no active item
    private var isAutoPlay = false
    private var isShareView = false

    fun rotateSwipedItem(item: ArrayList<Data>, colorList: ArrayList<String>) {

        colorArray.clear()
        colorArray.addAll(colorList)

        itemList.clear()
        itemList.addAll(item)

        notifyDataSetChanged()

    }

    fun isPlaying() = isPlaying

    fun changeItem(oldItem: Data, newItem: Data)
    {

        val index = itemList.indexOf(oldItem)
        if (index != -1) {
           /* suppressRecyclerViewAnimations {
                itemList[index] = newItem
                notifyItemChanged(index)
            }*/

            itemList[index] = newItem
            notifyItemChanged(index)
            
        }

    }

    fun updateAudioState(isPlaying:Boolean)
    {
        Log.e("updateAudioState",isPlaying.toString())
        this.isPlaying = isPlaying
        isNext = false
        isAutoPlay = false
        notifyDataSetChanged()
    }

    fun updateAudioStateAndNext(isPlaying:Boolean)
    {
        this.isPlaying = isPlaying
        isNext = true
        isAutoPlay = false
        notifyDataSetChanged()
    }

    fun getNameData(pos:Int): Data {
        return itemList[pos]
    }


    fun autoPlayItem()
    {
        isPlaying = false
        isAutoPlay = true
        notifyDataSetChanged()
    }

    fun shareView(visiblePosition: Int,isShareComplete:Boolean = false) {

        isShareView = !isShareComplete
        notifyItemChanged(visiblePosition)

    }

    fun getActiveData(pos:Int):Data = itemList[pos]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        val layout =  when(viewType)
        {
            MAINCARD -> R.layout.item_allah_99_names
            EMPTYCARD -> R.layout.item_allah_99_name_empty
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater
            .from(parent.context.getLocalContext())
            .inflate(layout, parent, false)

        Log.e("ALLAH99","CREATED")


        return  ViewHolder(view)
    }


    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return if(itemList[position].Name == "lastpage")
            EMPTYCARD
        else
            MAINCARD
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val nameCount:AppCompatTextView  by lazy { itemView.findViewById(R.id.nameCount) }
        private val nameBg:AppCompatImageView  by lazy { itemView.findViewById(R.id.nameBg) }
        private val nameArabic:AppCompatTextView by lazy { itemView.findViewById(R.id.nameArabic) }
        private val nameEnglish:AppCompatTextView by lazy { itemView.findViewById(R.id.nameEnglish) }
        private val nameBangla:AppCompatTextView by lazy { itemView.findViewById(R.id.nameBangla) }
        private val ic_next:AppCompatImageView by lazy { itemView.findViewById(R.id.ic_next) }
        private val ic_prev:AppCompatImageView by lazy { itemView.findViewById(R.id.ic_prev) }
        private val ic_play:AppCompatImageView by lazy { itemView.findViewById(R.id.ic_play) }
        private val playLoading:CircularProgressIndicator by lazy { itemView.findViewById(R.id.playLoading) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                MAINCARD ->
                {
                    if(!isShowHeadSerial)
                        nameCount.invisible()

                    val namedata = itemList[absoluteAdapterPosition]

                    if(isNext && absoluteAdapterPosition == 0)
                    {
                        isNext = false
                        isAutoPlay = true
                        callback?.allah99NameNextClicked(this)
                        Log.e("allah99NameNextClicked","OKK")
                    }



                    nameArabic.text = namedata.Arabic
                    nameEnglish.text = namedata.Name
                    nameBangla.text = namedata.Meaning

                    nameBg.setBackgroundColor(Color.parseColor(colorArray[absoluteAdapterPosition]))

                    nameCount.text = "${namedata.Serial}/99".numberLocale()

                    Log.e("bindview99name",namedata.Serial.toString())

                    ic_next.setOnClickListener {
                        callback?.allah99NameNextClicked(this)
                    }

                    ic_prev.setOnClickListener {
                        callback?.allah99NamePrevClicked(this)
                    }

                    ic_play.setOnClickListener {

                        if(isPlaying) {
                            callback?.allahNamePauseClicked()
                            return@setOnClickListener
                        }

                        ic_play.invisible()
                        playLoading.visible(true)
                        callback?.allahNamePlayClicked(namedata,absoluteAdapterPosition)
                        activePosition = absoluteAdapterPosition
                    }

                    if (position == 0) { // Assuming 0 is the topmost item.
                        itemView.isClickable = true
                        itemView.isFocusable = true
                    } else {
                        itemView.isClickable = false
                        itemView.isFocusable = false
                    }

                    if(isPlaying) {
                        playLoading.visible(false)
                        ic_play.setImageDrawable(
                            ContextCompat.getDrawable(
                                ic_play.context,
                                R.drawable.ic_pause_fill
                            )
                        )
                        ic_play.show()
                    }
                    else {

                        playLoading.visible(false)
                        ic_play.setImageDrawable(
                            ContextCompat.getDrawable(
                                ic_play.context,
                                R.drawable.ic_play_fill
                            )
                        )

                        ic_play.show()
                    }


                    if(isAutoPlay && absoluteAdapterPosition == 0)
                    {
                        isAutoPlay = false
                        ic_play.invisible()
                        playLoading.visible(true)
                        callback?.allahNamePlayClicked(namedata,-1)
                        Log.e("allah99isAutoPlay","OKK")
                    }

                    if(isShareView){
                        ic_prev.hide()
                        ic_play.hide()
                        ic_next.hide()
                    }
                    else{
                        ic_prev.show()
                        ic_play.show()
                        ic_next.show()
                    }

                    if(playLoading.isVisible)
                        ic_play.invisible()


                    itemView.setOnClickListener {
                        allah99NameCallback?.allahNameClicked(absoluteAdapterPosition)
                    }
                }

                EMPTYCARD ->
                {
                    nameBg.setBackgroundColor(Color.parseColor(colorArray[absoluteAdapterPosition]))

                    itemView.setOnClickListener {
                        allah99NameCallback?.allahNameClicked(absoluteAdapterPosition)
                    }
                }
            }

        }

    }
}