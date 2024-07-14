package com.deenislamic.sdk.service.network.response.quran.learning.quransikkhaacademy

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Keep
@Parcelize
internal data class ContentListResponse(
    val code: Int = 0,
    val data: Data,
    val msg: String = "",
    val status: String = ""
) : Parcelable {

    @Keep
    @Parcelize
    internal data class Data(
        val meta: Meta = Meta(),
        val results: List<Result> = listOf()
    ): Parcelable {

        @Keep
        @Parcelize
        internal data class Meta(
            val lastPage: Int = 0,
            val next: Int = 0,
            val pageSize: Int = 0,
            val previous: String? = null,
            val total: Int = 0
        ): Parcelable

        @Keep
        @Parcelize
        internal data class Result(
            val contentType: String = "",
            val extension: String? = null,
            val id: String = "",
            val isFree: Boolean = false,
            val isUnlocked: Boolean = false,
            val publicMetadata: PublicMetadata? = null,
            val title: String = "",
            var isPlaying: Boolean? = false,
            var isSelected: Boolean? = false
        ) : Parcelable {

            @Keep
            @Parcelize
            internal data class PublicMetadata(
                val duration: Int = 0
            ) : Parcelable {
                var durationContent: String
                    get() {
                        return getMSFromSecond(duration)
                    }
                    set(value) {
                        durationContent = value
                    }

                private fun getMSFromSecond(duration: Int): String {
                    val minutes = duration / 60
                    return String.format("ভিডিও • %d:%02d মি:", minutes, duration % 60)
                }
            }

        }

    }

}


