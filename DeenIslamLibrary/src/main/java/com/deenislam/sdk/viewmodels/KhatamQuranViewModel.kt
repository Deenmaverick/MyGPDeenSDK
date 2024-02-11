package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamicEducationVideoResource
import com.deenislam.sdk.service.models.KhatamQuranVideoResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.khatamquran.KhatamQuranVideosResponse
import com.deenislam.sdk.service.repository.KhatamEquranVideoRepository
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

internal class KhatamQuranViewModel(
    private val repository: KhatamEquranVideoRepository
) : ViewModel() {
    private val _khatamquranVideoLiveData: MutableLiveData<KhatamQuranVideoResource> =
        MutableLiveData()
    val khatamquranVideoLiveData: MutableLiveData<KhatamQuranVideoResource> get() = _khatamquranVideoLiveData

    private val _recentKhatamquranVideoLiveData: MutableLiveData<KhatamQuranVideoResource> =
        MutableLiveData()
    val recentKhatamquranVideoLiveData: MutableLiveData<KhatamQuranVideoResource> get() = _recentKhatamquranVideoLiveData


    private val _addHistoryLiveData: MutableLiveData<IslamicEducationVideoResource> =
        MutableLiveData()
    val addHistoryLiveData: MutableLiveData<IslamicEducationVideoResource> get() = _addHistoryLiveData

    fun getKhatamQuranVideo(language: String) {
        viewModelScope.launch {

                val response =
                    async { repository.getRecentKhatamQuranVideos(language = language) }.await()
                when (response) {
                    is ApiResource.Failure -> _recentKhatamquranVideoLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (response.value?.success == true)
                            _recentKhatamquranVideoLiveData.value =
                                KhatamQuranVideoResource.khatamequranRecentVideos(response.value.data)
                        else
                            _recentKhatamquranVideoLiveData.value = CommonResource.EMPTY
                    }
                }

            when (val response = repository.getKhatamQuranVideos(language = language)) {
                is ApiResource.Failure -> _khatamquranVideoLiveData.value =
                    CommonResource.API_CALL_FAILED

                is ApiResource.Success -> {
                    response.value?.let { writeToFile(it) }
                    if (response.value?.success == true)
                        _khatamquranVideoLiveData.value =
                            KhatamQuranVideoResource.khatamequranVideo(response.value.data)
                    else
                        _khatamquranVideoLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getKhatamQuranRecentVideos(language: String) {
        viewModelScope.launch {
            val response = async { repository.getRecentKhatamQuranVideos(language = language) }.await()
            when (response) {
                is ApiResource.Failure -> _recentKhatamquranVideoLiveData.value =
                    CommonResource.API_CALL_FAILED

                is ApiResource.Success -> {
                    if (response.value?.success == true)
                        _recentKhatamquranVideoLiveData.value =
                            KhatamQuranVideoResource.khatamequranRecentVideos(response.value.data)
                    else
                        _recentKhatamquranVideoLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun postKhatamQuranHistory(contentID: Int, totalDuration: Int,  duration: Int, language: String) {
        viewModelScope.launch {

            when (val response = repository.addKhatamQuranContentHistory(
                contentID, totalDuration, duration, language
            )) {
                is ApiResource.Failure -> _addHistoryLiveData.value =
                    CommonResource.API_CALL_FAILED

                is ApiResource.Success -> {
                    if (response.value?.Success == true)
                        _addHistoryLiveData.value =
                            IslamicEducationVideoResource.addHistoryDone(response.value.Success)
                    else
                        _addHistoryLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun clear() {
        _recentKhatamquranVideoLiveData.value = CommonResource.CLEAR
        _khatamquranVideoLiveData.value = CommonResource.CLEAR
    }

    private fun writeToFile(data: KhatamQuranVideosResponse) {

        if (data.data.isEmpty()) {
            return
        }
        val file = File(DeenSDKCore.appContext?.filesDir, "khatamquran")
        if (!file.exists()) {
            file.mkdir()
        }

        try {
            val gpxfile = File(file, "videos" + ".json")
            val writer = FileWriter(gpxfile)
            writer.append(Gson().toJson(data))
            writer.flush()
            writer.close()
        } catch (e: Exception) {
        }
    }
}