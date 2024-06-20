package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PodcastResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.podcast.comment.Comment
import com.deenislamic.sdk.service.repository.PodcastRepository
import com.deenislamic.sdk.service.repository.YoutubeVideoRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

internal class PodcastViewModel(
    private val youtubeVideoRepository: YoutubeVideoRepository,
    private val podcastRepository: PodcastRepository,
    private val quranLearningRepository: QuranLearningRepository
) : ViewModel() {

    private val _podcastLiveData: MutableLiveData<PodcastResource> = MutableLiveData()
    val podcastLiveData: MutableLiveData<PodcastResource> get() = _podcastLiveData

    private val _secureUrlLiveData: MutableLiveData<PodcastResource> = MutableLiveData()
    val secureUrlLiveData: MutableLiveData<PodcastResource> get() = _secureUrlLiveData

    private val _podcastCommentLiveData: MutableLiveData<PodcastResource> = MutableLiveData()
    val podcastCommentLiveData: MutableLiveData<PodcastResource> get() = _podcastCommentLiveData


    // Auto task
    private val _taskStatus = MutableLiveData<Boolean>()
    val taskStatus: LiveData<Boolean>
        get() = _taskStatus

    private val timer = Timer()
    private var timerTask: TimerTask? = null


    fun getYoutubeVideoDetails(videoid:String) {

        viewModelScope.launch {

            when(val response = youtubeVideoRepository.getVideoDetails(videoid)) {
                is ApiResource.Failure -> _podcastLiveData.value = PodcastResource.YoutubeVideoFetchFailed
                is ApiResource.Success -> {

                    val videourl = response.value?.getUrl("480p")

                    if(!videourl.isNullOrEmpty())
                        _podcastLiveData.value = PodcastResource.FetchLiveVideoUrl(videourl)
                    else
                        _podcastLiveData.value = PodcastResource.YoutubeVideoFetchFailed

                }
            }

        }
    }

    suspend fun getPodcastHomePatch(language:String){
        viewModelScope.launch {
            when(val response = podcastRepository.getPodcastHomePatch(language)){
                is ApiResource.Failure -> _podcastLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Data?.isNotEmpty() == true){
                        _podcastLiveData.value = PodcastResource.HomePatch(response.value.Data)
                    }
                    else
                        _podcastLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun getPodcastContent(langauage:String,pid:Int){

        viewModelScope.launch {
            when(val response = podcastRepository.getPodcastContent(langauage,pid)){
                is ApiResource.Failure -> _podcastLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Data?.Contents?.isNotEmpty() == true){
                        _podcastLiveData.value = PodcastResource.PodcastContent(response.value.Data)
                    }
                    else
                        _podcastLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun getPodcastCat(langauage:String,cid:Int){

        viewModelScope.launch {
            when(val response = podcastRepository.getPodcastCat(langauage,cid)){
                is ApiResource.Failure -> _podcastLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Data?.isNotEmpty() == true){
                        _podcastLiveData.value = PodcastResource.PodcastCat(response.value.Data)
                    }
                    else
                        _podcastLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun secureUrl(url:String)
    {
        viewModelScope.launch {

            when(val response = quranLearningRepository.getDigitalQuranSecureUrl(url))
            {
                is ApiResource.Failure -> _secureUrlLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->{
                    if(response.value?.url?.isNotEmpty() == true)
                        _secureUrlLiveData.value = PodcastResource.SecureUrl(response.value.url)
                    else _secureUrlLiveData.value =  CommonResource.API_CALL_FAILED
                }
            }

        }
    }

    suspend fun getAllComment(pid: Int, language: String){

        viewModelScope.launch {
            when(val response = podcastRepository.getPodcastAllComment(pid,language)){
                is ApiResource.Failure -> Unit
                is ApiResource.Success -> {
                    if(response.value?.Data?.comments?.isNotEmpty() == true){
                        _podcastCommentLiveData.value = PodcastResource.PodcastComment(response.value.Data)
                    }
                }
            }
        }
    }

    suspend fun likeComment(getdata: Comment, language: String) {
        val isfav = getdata.isLiked
        var favcount = getdata.LikeCount
        viewModelScope.launch {
            when(val response = podcastRepository.likeComment(getdata.Id,getdata.isLiked,language)){
                is ApiResource.Failure -> Unit
                is ApiResource.Success -> {
                    if(response.value?.Success == true) {

                        if(isfav)
                            favcount--
                        else
                            favcount++

                        if(favcount<0)
                            favcount = 0

                        _podcastLiveData.value =
                            PodcastResource.PodcastCommentLiked(getdata.copy(isLiked = !isfav, LikeCount = favcount))
                    }
                }
            }
        }
    }

    suspend fun addComment(pid:Int,comment:String) {
        viewModelScope.launch {
            when(val response = podcastRepository.addComment(pid,comment)){
                is ApiResource.Failure -> _podcastCommentLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Data?.comments?.isNotEmpty() == true) {
                        _podcastCommentLiveData.value = PodcastResource.PodcastAddComment(response.value.Data)
                    }else
                        _podcastCommentLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    suspend fun startTask(pid: Int, language: String) {
        timerTask = object : TimerTask() {
            override fun run() {
                viewModelScope.launch {
                    getAllComment(pid,language)
                }

            }
        }
        timer.schedule(timerTask, 0, 15000) // Schedule the task to run every 5 seconds
    }

    override fun onCleared() {
        super.onCleared()
        stopTask()
    }

    private fun stopTask() {
        timerTask?.cancel()
        timer.cancel()
    }

}