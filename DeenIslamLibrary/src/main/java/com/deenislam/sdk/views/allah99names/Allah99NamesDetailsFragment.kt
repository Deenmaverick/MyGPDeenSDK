package com.deenislam.sdk.views.allah99names

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.Allah99NameCallback
import com.deenislam.sdk.service.callback.AudioManagerBasicCallback
import com.deenislam.sdk.service.libs.cardstackview.StackLayoutManager
import com.deenislam.sdk.service.libs.media3.AudioManager
import com.deenislam.sdk.service.network.response.allah99name.Data
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.shareView
import com.deenislam.sdk.views.adapters.allah99names.Allah99NamesHomeAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback


internal class Allah99NamesDetailsFragment : BaseRegularFragment(), Allah99NameCallback,
    AudioManagerBasicCallback, otherFagmentActionCallback {


    private lateinit var listView: RecyclerView
    private lateinit var progressLayout: LinearLayout
    private lateinit var actionbar: ConstraintLayout
    private lateinit var container: NestedScrollView
    private lateinit var allah99NamesHomeAdapter: Allah99NamesHomeAdapter
    private lateinit var fazilatTxt:AppCompatTextView
    private var nameItemList: ArrayList<Data> = arrayListOf()
    private val navArgs:Allah99NamesDetailsFragmentArgs by navArgs()

    private var swipeItemCount = -1
    private var swipeColorCount = 0

    private var isItemUpdateRightSwipe = false
    private var isItemUpdateLeftSwipe = false
    private var stackLayoutManager: StackLayoutManager? =null
    private var itemTouchHelper:ItemTouchHelper ? = null
    private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback ?  = null
    private var isItemAlreadyAnim = false
    private var isAutoPlay = false
    private var isManualSwipe = false


    val colorArray = arrayListOf(
        "#1E8787",
        "#2FB68E",
        "#28AEAE"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        CallBackProvider.setFragment(this)
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_allah99_names_details,container,false)

        //init view

        listView = mainView.findViewById(R.id.listView)
        progressLayout = mainView.findViewById(R.id.progressLayout)
        actionbar = mainView.findViewById(R.id.actionbar)
        fazilatTxt = mainView.findViewById(R.id.fazilatTxt)

        this.container = mainView.findViewById(R.id.container)
        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = this@Allah99NamesDetailsFragment,
            actionnBartitle = localContext.getString(R.string.allah_99_name),
            backEnable = true,
            view = mainView
        )

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else
            loadpage()

    }

    private fun loadpage()
    {
        ViewCompat.setTranslationZ(progressLayout, 10F)


        /*actionbar.post {
            val param = container.layoutParams as ViewGroup.MarginLayoutParams
            param.topMargin = container.marginTop+ actionbar.height
            container.layoutParams = param
        }*/


        nameItemList = ArrayList(navArgs.nameList.toList())

        fazilatTxt.text = nameItemList[navArgs.namePos].Fazilat



        allah99NamesHomeAdapter = Allah99NamesHomeAdapter()

        listView.apply {

            this.itemAnimator?.changeDuration = 0

            post {
                stackLayoutManager = StackLayoutManager(this.context)
                layoutManager = stackLayoutManager
                adapter = allah99NamesHomeAdapter
                swipeItemCount = navArgs.namePos-1

                allah99NamesHomeAdapter.rotateSwipedItem(
                    nextNameSubList(nameItemList),
                    nextColorSubList(colorArray)
                )

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

                        Log.e("Allah99onChildDraw","OK")
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

                        var topItemPosition = viewHolder.absoluteAdapterPosition

                        if(isManualSwipe) {
                            AudioManager().getInstance()
                                .pauseMediaPlayer(viewHolder.absoluteAdapterPosition)
                            allah99NamesHomeAdapter.autoPlayItem()
                        }
                        isAutoPlay = true

                        if(topItemPosition<0)
                            topItemPosition = 0

                        isItemUpdateLeftSwipe = false
                        isItemUpdateRightSwipe = false

                        when (direction) {
                            ItemTouchHelper.LEFT -> {
                                allah99NamesHomeAdapter.rotateSwipedItem(
                                    nextNameSubList(nameItemList),
                                    nextColorSubList(colorArray)
                                )
                                // Item was swiped to the left
                            }
                            ItemTouchHelper.RIGHT -> {

                                allah99NamesHomeAdapter.rotateSwipedItem(
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

                        fazilatTxt.text = allah99NamesHomeAdapter.getActiveData(topItemPosition).Fazilat



                    }

                }

                itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback as ItemTouchHelper.SimpleCallback)
                itemTouchHelper?.attachToRecyclerView(this)

                progressLayout.hide()
            }

        }


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
        allah99NamesHomeAdapter.changeItem(prevItem, nextItem)
    }

    private fun preChangePrevItem()
    {
        val nextItem =
            nameItemList[(swipeItemCount - 1 + nameItemList.size) % nameItemList.size]
        val prevItem =
            nameItemList[(swipeItemCount + 1) % nameItemList.size]
        allah99NamesHomeAdapter.changeItem(prevItem, nextItem)
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
                        allah99NamesHomeAdapter.autoPlayItem()
                    else
                        AudioManager().getInstance().stopMediaPlayer()
                // Once the animation ends, inform the ItemTouchHelper.
                itemTouchHelperCallback?.onSwiped(viewHolder, direction)
                viewHolder.itemView.translationX = 0f  // Reset translation.

                Log.e("99withEndAction","Ok")

            }
            .start()
    }



    override fun allah99NameNextClicked(viewHolder: RecyclerView.ViewHolder) {
        //AudioManager().getInstance().stopMediaPlayer()
        preChangeNextItem()
        swipeItem(viewHolder,ItemTouchHelper.LEFT)
    }

    override fun allah99NamePrevClicked(viewHolder: RecyclerView.ViewHolder) {
        //AudioManager().getInstance().stopMediaPlayer()
        preChangePrevItem()
        swipeItem(viewHolder,ItemTouchHelper.RIGHT)
    }

    override fun allahNameClicked(position: Int) {

    }

    override fun allahNamePlayClicked(namedata: Data, absoluteAdapterPosition: Int) {

        Log.e("isMedia3PlayComplete",absoluteAdapterPosition.toString())

        if(absoluteAdapterPosition!=-1)
            isAutoPlay = true
        isManualSwipe = false
        //requireContext().toast("No audio file from server")
        AudioManager().getInstance().playAudioFromUrl(BASE_CONTENT_URL_SGP+namedata.ContentUrl)
    }

    override fun allahNamePauseClicked() {
        isAutoPlay = false
        AudioManager().getInstance().stopMediaPlayer()
    }

    override fun isMedia3PlayComplete() {

       /* if(isAutoPlay)
        allah99NamesHomeAdapter.updateAudioStateAndNext(isPlaying = false)
        else*/
            allah99NamesHomeAdapter.updateAudioState(isPlaying = false)

        if(isAutoPlay) {
            listView.post {
                // Notify the callback
                val viewHolder = listView.findViewHolderForAdapterPosition(0)
                if (viewHolder != null) {
                    allah99NameNextClicked(viewHolder)
                }
            }
        }
        else
        {
            Log.e("isMedia3PlayComplete","MANUAL")
            listView.post {
                isManualSwipe = false
                isAutoPlay = true
                allah99NamesHomeAdapter.autoPlayItem()
            }
        }

    }

    override fun isMedia3Pause() {
        allah99NamesHomeAdapter.updateAudioState(isPlaying = false)
    }

    override fun isMedia3Playing() {
        allah99NamesHomeAdapter.updateAudioState(isPlaying = true)
    }

    override fun isMedia3Stop() {
        allah99NamesHomeAdapter.updateAudioState(isPlaying = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        AudioManager().getInstance().stopMediaPlayer()
    }

    override fun action1() {
        val visiblePosition = 0
        allah99NamesHomeAdapter.shareView(visiblePosition)

        listView.post {
            val viewHolder = listView.findViewHolderForAdapterPosition(visiblePosition)
            val itemView = viewHolder?.itemView

            if (itemView != null) {
                requireContext().shareView(itemView)
            }
            allah99NamesHomeAdapter.shareView(visiblePosition,true)
        }
    }

    override fun action2() {
    }

}