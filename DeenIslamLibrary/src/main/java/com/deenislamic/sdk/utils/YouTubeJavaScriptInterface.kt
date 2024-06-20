package com.deenislamic.sdk.utils

import android.webkit.JavascriptInterface
import com.deenislamic.sdk.service.callback.common.YoutubeWebViewCallback

internal class YouTubeJavaScriptInterface(private val callback: YoutubeWebViewCallback) {

    @JavascriptInterface
    fun onFullScreenButtonClick() {
        callback.onYoutubeFullscreenClicked()
    }

    @JavascriptInterface
    fun onReady() {
        callback.onYoutubeReadyState()
    }

    @JavascriptInterface
    fun onPlaying() {
        callback.onYoutubePlayingState()
    }

    @JavascriptInterface
    fun onPause() {
        callback.onYoutubePauseState()
    }

    @JavascriptInterface
    fun onEnded() {
        callback.onYoutubeEndedState()
    }

}
