package com.deenislamic.sdk.views.dashboard.patch;

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.Allah99NameCallback
import com.deenislamic.sdk.service.callback.AudioManagerBasicCallback
import com.deenislamic.sdk.service.libs.cardstackview.StackLayoutManager
import com.deenislamic.sdk.service.libs.media3.AudioManager
import com.deenislamic.sdk.service.network.response.allah99name.Data
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.adapters.allah99names.Allah99NamesHomeAdapter
import com.google.gson.Gson

internal class Allah99Names(itemView: View): Allah99NameCallback,
    AudioManagerBasicCallback {

    private val nameList: RecyclerView = itemView.findViewById(R.id.nameList)
    private val icon: AppCompatImageView = itemView.findViewById(R.id.icon)
    private var itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private var allah99NamesHomeAdapter: Allah99NamesHomeAdapter? = null
    private var swipeItemCount = -1
    private var swipeColorCount = 0
    private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback ?  = null
    private var itemTouchHelper:ItemTouchHelper ? = null
    private var isItemUpdateRightSwipe = false
    private var isItemUpdateLeftSwipe = false
    private var isAutoPlay = false
    private var nameItemList: ArrayList<Data> = arrayListOf()
    private var isManualSwipe = false
    //private var actionCount = 0

    private val audioManager: AudioManager = AudioManager().getInstance()

    fun reInitCallback()
    {
        audioManager.setCustomCallback(this)
    }

    fun stop99NamePlaying()
    {
        if(audioManager.getMediaPlayer()?.isPlaying == true && allah99NamesHomeAdapter?.isPlaying() == true)
            audioManager.stopMediaPlayer()
    }

    fun load(items: List<Item>) {

        reInitCallback()

        val data = items[0]

        icon.show()

        icon.let {
            it.setImageDrawable(
                AppCompatResources.getDrawable(
                    it.context,
                    R.drawable.deen_ic_menu_99_name_of_allah
                )
            )
        }

        Log.e("99name",Gson().toJson(items))

        itemTitle.show()
        itemTitle.text = data.FeatureTitle

        allah99NamesHomeAdapter = Allah99NamesHomeAdapter(false,this@Allah99Names)


         nameItemList = ArrayList(items.map { transformNameData(it) })

        val colorArray = arrayListOf(
            "#1E8787",
            "#2FB68E",
            "#28AEAE"
        )

        nameList.apply {

            this.itemAnimator?.changeDuration = 0

            val stackLayoutManager = StackLayoutManager(this.context)

            adapter = allah99NamesHomeAdapter
            layoutManager = stackLayoutManager
            overScrollMode = View.OVER_SCROLL_NEVER
            ViewPagerHorizontalRecyler().getInstance().load(this)




            itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    isAutoPlay = false
                    isManualSwipe = true
                    val swipeThreshold = 0.20 * viewHolder.itemView.width


                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
                        if (dX > swipeThreshold) {
                            // User is swiping to the right
                            if(!isItemUpdateRightSwipe) {
                                preChangePrevItem()
                                isItemUpdateLeftSwipe = false
                                isItemUpdateRightSwipe = true
                                Log.e("onChildDraw","RIGHT")
                            }

                        } else if (dX < -swipeThreshold) {
                            // User is swiping to the left


                            if(!isItemUpdateLeftSwipe) {
                                preChangeNextItem()
                                isItemUpdateLeftSwipe = true
                                isItemUpdateRightSwipe = false

                                Log.e("onChildDraw","LEFT")
                            }

                        }
                    }
                }


                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    //checkTotalAction()
                    //var topItemPosition = viewHolder.absoluteAdapterPosition

                    if(isManualSwipe) {
                        audioManager
                            .pauseMediaPlayer(viewHolder.absoluteAdapterPosition)
                        allah99NamesHomeAdapter?.autoPlayItem()
                    }
                    isAutoPlay = true


                    isItemUpdateLeftSwipe = false
                    isItemUpdateRightSwipe = false

                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            allah99NamesHomeAdapter?.rotateSwipedItem(
                                nextNameSubList(nameItemList),
                                nextColorSubList(colorArray)
                            )
                            // Item was swiped to the left
                        }
                        ItemTouchHelper.RIGHT -> {

                            allah99NamesHomeAdapter?.rotateSwipedItem(
                                nextNameSubList(nameItemList, isSwipeRight = true),
                                nextColorSubList(colorArray)
                            )
                            Log.e("ItemTouchHelper",swipeItemCount.toString())


                            // Item was swiped to the right
                        }
                        // You can also handle UP and DOWN if needed
                        // ItemTouchHelper.UP -> { }
                        // ItemTouchHelper.DOWN -> { }
                        else -> { /* Handle other directions if needed */ }
                    }


                }

                override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                    // Return a fraction (0 to 1) that represents the amount of swipe needed
                    // Adjust this value to make the swipe more or less sensitive
                    return 0.3f // Example: requires 20% of the view to trigger a swipe
                }

            }


            itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback as ItemTouchHelper.SimpleCallback)
            itemTouchHelper?.attachToRecyclerView(this)

            post {
                if(nameItemList.size>0) {
                    allah99NamesHomeAdapter?.rotateSwipedItem(
                        nextNameSubList(nameItemList),
                        nextColorSubList(colorArray)
                    )
                }
            }

        }
    }


    fun transformNameData(newDataModel: Item?): Data {

        return Data(
            Arabic =  newDataModel?.ArabicText.toString(),
            ContentUrl = newDataModel?.imageurl2.toString(),
            Fazilat = "",
            Id = newDataModel?.Id?:0,
            ImageUrl = "",
            Meaning = newDataModel?.Reference.toString(),
            Name = newDataModel?.Text.toString(),
            Serial = newDataModel?.Sequence?:0
        )
    }
    private fun nextNameSubList(nameList: ArrayList<Data>, isSwipeRight: Boolean=false): ArrayList<Data> {
        val sublist = ArrayList<Data>()

        // Decide the next value for the counter
        swipeItemCount = if (isSwipeRight) {
            (swipeItemCount - 1 + nameList.size) % nameList.size
        } else {
            (swipeItemCount + 1) % nameList.size
        }

        for (i in 0 until 3) {
            val index = (swipeItemCount + i) % nameList.size
            sublist.add(nameList[index])
        }

        return sublist
    }

    private fun nextColorSubList(colorList: ArrayList<String>): ArrayList<String> {
        val sublist = ArrayList<String>()

        for (i in 0 until 3) {  // We want to fetch 3 items
            val index = (swipeColorCount + i) % colorList.size // Use modulus to wrap around
            sublist.add(colorList[index])
        }

        swipeColorCount = (swipeColorCount + 1) % colorList.size

        return sublist
    }


    private fun preChangeNextItem()
    {
        val prevItem =
            nameItemList[(swipeItemCount - 1 + nameItemList.size) % nameItemList.size]
        val nextItem =
            nameItemList[(swipeItemCount + 1) % nameItemList.size]
        allah99NamesHomeAdapter?.changeItem(prevItem, nextItem)
    }

    private fun preChangePrevItem()
    {
        val nextItem =
            nameItemList[(swipeItemCount - 1 + nameItemList.size) % nameItemList.size]
        val prevItem =
            nameItemList[(swipeItemCount + 1) % nameItemList.size]
        allah99NamesHomeAdapter?.changeItem(prevItem, nextItem)
    }

    fun swipeItem(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Here we'll initiate a swipe animation.
        // direction should be either ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT.
        val targetTranslationX = if (direction == ItemTouchHelper.LEFT) {
            -1 * viewHolder.itemView.width.toFloat()
        } else {
            viewHolder.itemView.width.toFloat()
        }

        viewHolder.itemView.animate()
            .translationX(targetTranslationX)
            .setDuration(300)  // Adjust duration as needed.
            .withEndAction {

                if(isAutoPlay)
                    allah99NamesHomeAdapter?.autoPlayItem()
                else
                    audioManager.stopMediaPlayer()
                // Once the animation ends, inform the ItemTouchHelper.
                itemTouchHelperCallback?.onSwiped(viewHolder, direction)
                viewHolder.itemView.translationX = 0f  // Reset translation.

                Log.e("99withEndAction","Ok")

            }
            .start()
    }



    override fun allah99NameNextClicked(viewHolder: RecyclerView.ViewHolder) {
        //checkTotalAction()
        //AudioManager().getInstance().stopMediaPlayer()
        preChangeNextItem()
        swipeItem(viewHolder,ItemTouchHelper.LEFT)
    }

    override fun allah99NamePrevClicked(viewHolder: RecyclerView.ViewHolder) {
        //checkTotalAction()
        //AudioManager().getInstance().stopMediaPlayer()
        preChangePrevItem()
        swipeItem(viewHolder,ItemTouchHelper.RIGHT)
    }

    override fun allahNameClicked(position: Int) {

    }

    override fun allahNamePlayClicked(namedata: Data, absoluteAdapterPosition: Int) {
        //checkTotalAction()
        Log.e("isMedia3PlayCompleteH",absoluteAdapterPosition.toString())
        if(absoluteAdapterPosition!=-1)
            isAutoPlay = true
        isManualSwipe = false
        //requireContext().toast("No audio file from server")
        audioManager.playAudioFromUrl(BASE_CONTENT_URL_SGP +namedata.ContentUrl)
    }

    override fun allahNamePauseClicked() {
        //checkTotalAction()
        isAutoPlay = false
        audioManager.stopMediaPlayer()
    }

    override fun isMedia3PlayComplete() {

        Log.e("isMedia3PlayCompleteM","MANUAL")
        allah99NamesHomeAdapter?.updateAudioState(isPlaying = false)

        if(isAutoPlay) {
            nameList.post {
                // Notify the callback
                val viewHolder = nameList.findViewHolderForAdapterPosition(0)
                if (viewHolder != null) {
                    allah99NameNextClicked(viewHolder)
                }
            }
        }
        else
        {
            nameList.post {
                isManualSwipe = false
                isAutoPlay = true
                allah99NamesHomeAdapter?.autoPlayItem()
            }
        }

    }

    override fun isMedia3Pause() {
        allah99NamesHomeAdapter?.updateAudioState(isPlaying = false)
    }

    override fun isMedia3Playing() {
        allah99NamesHomeAdapter?.updateAudioState(isPlaying = true)
    }

    override fun isMedia3Stop() {
        allah99NamesHomeAdapter?.updateAudioState(isPlaying = false)
    }

   /* private fun checkTotalAction()
    {
        actionCount++

        if(actionCount>=10) {
            allah99NameCallback?.allahNameClicked(0)
            actionCount = 0
        }
    }*/

}
