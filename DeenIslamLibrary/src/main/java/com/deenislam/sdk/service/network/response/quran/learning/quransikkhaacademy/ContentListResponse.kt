package com.deenislam.sdk.service.network.response.quran.learning.quransikkhaacademy

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
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val `data`: @RawValue Data,
    @SerializedName("msg")
    val msg: String = "",
    @SerializedName("status")
    val status: String = ""
) : Parcelable {

    @Keep
    internal data class Data(
        @SerializedName("meta")
        val meta: Meta = Meta(),
        @SerializedName("results")
        val results: List<Result> = listOf()
    ) {

        @Keep
        internal data class Meta(
            @SerializedName("last_page")
            val lastPage: Int = 0,
            @SerializedName("next")
            val next: Int = 0,
            @SerializedName("page_size")
            val pageSize: Int = 0,
            @SerializedName("previous")
            val previous: Any? = null,
            @SerializedName("total")
            val total: Int = 0
        )

        @Keep
        @Parcelize
        internal data class Result(
            @SerializedName("content_type")
            val contentType: String = "",
            @SerializedName("extension")
            val extension: String? = null,
            @SerializedName("_id")
            val id: String = "",
            @SerializedName("is_free")
            val isFree: Boolean = false,
            @SerializedName("is_unlocked")
            val isUnlocked: Boolean = false,
            @SerializedName("public_metadata")
            val publicMetadata: @RawValue PublicMetadata? = null,
            @SerializedName("title")
            val title: String = "",
            var isPlaying: Boolean? = false,
            var isSelected: Boolean? = false
        ) : Parcelable {

            @Keep
            @Parcelize
            internal data class PublicMetadata(
                @SerializedName("duration")
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

            fun toMediaItem(url: String) = MediaItem.Builder()
                .setMediaId(id)
                .setUri(url)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(title)

                        .build()
                )
                .build()
        }

    }

}

