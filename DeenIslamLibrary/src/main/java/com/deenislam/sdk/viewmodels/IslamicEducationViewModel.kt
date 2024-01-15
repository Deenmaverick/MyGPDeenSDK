package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamicEducationVideoResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.IslamicEducationVideoRepository
import kotlinx.coroutines.launch


internal class IslamicEducationViewModel(
    private val repository: IslamicEducationVideoRepository
) : ViewModel() {
    private val _educationVideoLiveData: MutableLiveData<IslamicEducationVideoResource> =
        MutableLiveData()
    val educationVideo: MutableLiveData<IslamicEducationVideoResource> get() = _educationVideoLiveData

    private val _addHistoryLiveData: MutableLiveData<IslamicEducationVideoResource> =
        MutableLiveData()
    val addHistoryLiveData: MutableLiveData<IslamicEducationVideoResource> get() = _addHistoryLiveData

    fun getIslamicEducationVideo(language: String) {
        viewModelScope.launch {

            when (val response = repository.getIslamicEducationVideos(language = language)) {
                is ApiResource.Failure -> _educationVideoLiveData.value =
                    CommonResource.API_CALL_FAILED

                is ApiResource.Success -> {
                    if (response.value?.Success == true)
                        _educationVideoLiveData.value =
                            IslamicEducationVideoResource.educationVideo(response.value.Data)
                    else
                        _educationVideoLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun postIslamicVideoHistory(category: Int, content: Int, language: String) {
        viewModelScope.launch {

            when (val response = repository.addIslamicContentHistory(
                category = category,
                content = content,
                language = language
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
}