package com.deenislamic.sdk.views.adapters.quran.learning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.QuranLearningCallback
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.CourseConten
import com.deenislamic.sdk.service.network.response.quran.learning.digital_quran_class.Data

internal class QuranLearningCourseV2Adapter(data: Data) : RecyclerView.Adapter<BaseViewHolder>() {
    private val callback = CallBackProvider.get<QuranLearningCallback>()
    private var courseData:ArrayList<CourseConten> = ArrayList(data.courseContens)
    private var lastUpdatedItemID = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_course_curriculum, parent, false)
        )

    override fun getItemCount(): Int = courseData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun getFirstVideo(): CourseConten?
    {
        return if(courseData.isNotEmpty()) {
            courseData[0]
        } else null
    }

    fun updateData(newdata: CourseConten)
    {
        val oldList = courseData.toList()

        val matchingVideoDataIndex = oldList.indexOfFirst { it.ContentId == newdata.ContentId }

        if(lastUpdatedItemID!=-1)
            courseData[lastUpdatedItemID] = courseData[lastUpdatedItemID].copy(isPlaying = !courseData[lastUpdatedItemID].isPlaying)

        if(matchingVideoDataIndex!=-1) {
            lastUpdatedItemID = matchingVideoDataIndex
            courseData[matchingVideoDataIndex] = newdata

        }

        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                oldList,
                courseData
            )
        )

        diffResult.dispatchUpdatesTo(this)

    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val pos: AppCompatTextView by lazy { itemView.findViewById(R.id.pos) }
        private val title: AppCompatTextView by lazy { itemView.findViewById(R.id.title) }
        private val subTitle: AppCompatTextView by lazy { itemView.findViewById(R.id.subTitle) }
        private val icContentType: AppCompatImageView by lazy { itemView.findViewById(R.id.icContentType) }
        private val icPlay: AppCompatImageView by lazy { itemView.findViewById(R.id.icPlay) }
        private val quizLayout:ConstraintLayout by lazy { itemView.findViewById(R.id.quizLayout) }
        private val subLayout:ConstraintLayout by lazy { itemView.findViewById(R.id.subLayout) }

        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = courseData[position]
            pos.text = "${position + 1}".numberLocale()
            title.text = getData.ContenTtitle


            if(getData.ShowQuiz) {
                subTitle.text = "${getData.Duration} • "
                quizLayout.show()
            }
            else {
                subTitle.text = getData.Duration
                quizLayout.hide()
            }

            icContentType.load(R.drawable.deen_ic_video_play_icon)
            if(getData.Status)
                icPlay.load(
                    if(getData.isPlaying)
                        R.drawable.deen_ic_paused_circle
                    else
                        R.drawable.deen_ic_play_paused_circle
                )

           /* if(!getData.IsQuiz) {
                subTitle.text = getData.publicMetadata?.durationContent
                icContentType.load(R.drawable.ic_video_play_icon)
                if(getData.isUnlocked)
                    icPlay.load(
                        if(getData.isPlaying == true)
                            R.drawable.ic_paused_circle
                        else
                            R.drawable.ic_play_paused_circle
                    )
            }
            else {
                subTitle.text = "নোট • ১ ফাইল"
                if(getData.isUnlocked)
                    icPlay.load(R.drawable.ic_next)
                icContentType.load(R.drawable.ic_course_note)
            }*/

            if(!getData.Status)
                icPlay.load(R.drawable.deen_ic_lock)


            itemView.setOnClickListener {
                callback?.courseCurriculumClickedV2(getData)
            }

            quizLayout.setOnClickListener {
                callback?.courseQuizClicked(getData)
            }

        }
    }

    internal class DataDiffCallback(
        private val oldList: List<CourseConten>,
        private val newList: ArrayList<CourseConten>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].ContentId == newList[newItemPosition].ContentId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}