package com.deenislamic.sdk.service.callback

import android.graphics.Bitmap
import com.deenislamic.sdk.service.models.share.ColorList
import com.deenislamic.sdk.service.network.response.share.Data

internal interface ShareCallback {
    fun selectedTextColor(absoluteAdapterPosition: Int, getData: ColorList)
    fun selectedWallpaper(absoluteAdapterPosition: Int, bitmap: Bitmap?, data: Data?){

    }
}