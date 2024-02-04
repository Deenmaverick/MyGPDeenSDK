package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.PodcastResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.YoutubeVideoRepository
import kotlinx.coroutines.launch

internal class PodcastViewModel(
    private val youtubeVideoRepository: YoutubeVideoRepository
) : ViewModel() {

    private val _podcastLiveData: MutableLiveData<PodcastResource> = MutableLiveData()
    val podcastLiveData: MutableLiveData<PodcastResource> get() = _podcastLiveData

    fun getYoutubeVideoDetails(videoid:String)
    {
        viewModelScope.launch {

        when(val response = youtubeVideoRepository.getVideoDetails(videoid))
        {
            is ApiResource.Failure -> _podcastLiveData.value = PodcastResource.YoutubeVideoFetchFailed
            is ApiResource.Success ->
            {
                response.value?.getUrl("480p")?.let {
                    _podcastLiveData.value = PodcastResource.FetchLiveVideoUrl(it)
                }?:
                {
                    _podcastLiveData.value = PodcastResource.YoutubeVideoFetchFailed
                }
            }
        }


        }
    }


}