package com.deenislam.sdk.service.libs.advertisement

import android.util.Log
import com.deenislam.sdk.utils.Subscription

internal object Advertisement {

    var adData:MutableList<com.deenislam.sdk.service.network.response.advertisement.Data> = arrayListOf()
    var videoAdData:MutableList<com.deenislam.sdk.service.network.response.advertisement.Data> = arrayListOf()
    var imageAdData:MutableList<com.deenislam.sdk.service.network.response.advertisement.Data> = arrayListOf()

    private val providedImageAdIndices = mutableListOf<Int>()

    fun getImageAd(): com.deenislam.sdk.service.network.response.advertisement.Data? {
        if(Subscription.isSubscribe)
            return null
        if (imageAdData.isNotEmpty()) {
            if (providedImageAdIndices.size >= imageAdData.size) {
                // Reset provided indices and reshuffle the list if all items have been provided at least once
                providedImageAdIndices.clear()
                imageAdData.shuffle()
            }
            // Find an index that has not been provided yet
            var index = 0
            while (providedImageAdIndices.contains(index)) {
                index++
            }
            // Add the index to the provided indices
            providedImageAdIndices.add(index)
            // Return the corresponding element from the shuffled list
            return imageAdData[index]
        }
        return null
    }
}