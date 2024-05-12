package com.deenislam.sdk.viewmodels.quran.learning;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.quran.learning.quransikkhaacademy.ContentListResponse
import com.deenislam.sdk.service.repository.quran.learning.QuranLearningRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


internal class QuranLearningViewModel (
    private val quranLearningRepository: QuranLearningRepository
) : ViewModel() {

    private val _quranShikkhaAcademyLiveData:MutableLiveData<QuranLearningResource> = MutableLiveData()
    val quranShikkhaAcademyLiveData:MutableLiveData<QuranLearningResource> get() = _quranShikkhaAcademyLiveData

    private val _quranShikkhaContentLiveData:MutableLiveData<QuranLearningResource> = MutableLiveData()
    val quranShikkhaContentLiveData:MutableLiveData<QuranLearningResource> get() = _quranShikkhaContentLiveData

    private val _quranLearningLiveData:MutableLiveData<QuranLearningResource> = MutableLiveData()
    val quranLearningLiveData:MutableLiveData<QuranLearningResource> get() = _quranLearningLiveData

    private var accessToken = ""

    private val _quranLearningQuizLiveData:MutableLiveData<QuranLearningResource> = MutableLiveData()
    val quranLearningQuizLiveData:MutableLiveData<QuranLearningResource> get() = _quranLearningQuizLiveData


    suspend fun qsaGetContentList()
    {
        viewModelScope.launch {

            val getBotUser = async { quranLearningRepository.getUserInfo(DeenSDKCore.GetDeenMsisdn()) }
            val botUser = getBotUser.await()

            accessToken = when(botUser) {
                is ApiResource.Failure -> ""
                is ApiResource.Success -> {
                    botUser.value?.data?.user_token?.accesstoken.toString()
                }
            }

            when(val response = quranLearningRepository.getContentList(token = accessToken))
            {
                is ApiResource.Failure -> _quranShikkhaAcademyLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                {
                    if(response.value?.code == 200)
                        _quranShikkhaAcademyLiveData.value = QuranLearningResource.QSAContentList(response.value.data)
                    else
                        _quranShikkhaAcademyLiveData.value = CommonResource.API_CALL_FAILED

                }
            }
        }
    }

    suspend fun qsaOrderConfirm()
    {
        viewModelScope.launch {

                when(quranLearningRepository.botMyOrder(DeenSDKCore.GetDeenMsisdn()))
                {
                    is ApiResource.Failure -> _quranShikkhaAcademyLiveData.value = QuranLearningResource.QSAOrderConfirmationFailed
                    is ApiResource.Success -> _quranShikkhaAcademyLiveData.value = QuranLearningResource.QSAOrderSuccess
                }
        }
    }

    suspend fun qsaGetContentByID(
        id:String,
        contentData: ContentListResponse.Data.Result,
    )
    {
        viewModelScope.launch {

            when(val response = quranLearningRepository.getContentByID(token = accessToken, id = id))
            {
                is ApiResource.Failure -> _quranShikkhaContentLiveData.value = QuranLearningResource.QSAContentByIDFailed
                is ApiResource.Success ->{

                    if(response.value?.code == 200)
                        _quranShikkhaContentLiveData.value = QuranLearningResource.QSAContentByID(response.value.data,contentData)
                    else
                        _quranShikkhaContentLiveData.value = QuranLearningResource.QSAContentByIDFailed
                }
            }
        }
    }

    fun clear()
    {
        _quranShikkhaAcademyLiveData.value = CommonResource.CLEAR
        _quranShikkhaContentLiveData.value = CommonResource.CLEAR
    }

    suspend fun getHomePatch(language:String)
    {
        viewModelScope.launch {
            when(val response = quranLearningRepository.getHomePatch(language))
            {
                is ApiResource.Failure -> _quranLearningLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _quranLearningLiveData.value = QuranLearningResource.HomePatch(response.value.Data)
                    else
                        _quranLearningLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    suspend fun getDigitalQuranClass(language: String, courseId: Int)
    {
        viewModelScope.launch {
            when(val response = quranLearningRepository.getDigitalQuranClass(language,courseId))
            {
                is ApiResource.Failure -> _quranLearningLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Success == true)
                        _quranLearningLiveData.value = QuranLearningResource.DigitalQuranClass(response.value.Data)
                    else
                        _quranLearningLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    suspend fun getDigitalQuranSecureUrl(url:String)
    {
        viewModelScope.launch {

            when(val response = quranLearningRepository.getDigitalQuranSecureUrl(url))
            {
                is ApiResource.Failure -> Unit
                is ApiResource.Success ->{
                    if(response.value?.url?.isNotEmpty() == true)
                        _quranLearningLiveData.value = QuranLearningResource.QuranClassSecureUrl(response.value.url)
                    else Unit
                }
            }

        }
    }

    suspend fun getQuranClassQuizQuestion(language: String, courseId: Int,contentID:Int)
    {
        viewModelScope.launch {
            when(val response =  quranLearningRepository.getQuranClassQuizQuestion(
                language = language,
                courseId = courseId,
                contentID = contentID
            ))
            {
                is ApiResource.Failure -> _quranLearningQuizLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Data?.isNotEmpty() == true)
                        _quranLearningQuizLiveData.value = QuranLearningResource.QuranClassQuizQuestion(response.value.Data)
                    else
                        _quranLearningQuizLiveData.value = CommonResource.API_CALL_FAILED

                }
            }
        }
    }

    suspend fun updateQuranClassVideoWatch(
        language: String,
        courseId: Int,
        contentID:Int,
        duration:Int,
        watchDuration: Long
    )
    {
        viewModelScope.launch {



            val getresponse = async { quranLearningRepository.updateQuranClassVideoWatch(
                msisdn = DeenSDKCore.GetDeenMsisdn(),
                language = language,
                courseId = courseId,
                contentID = contentID,
                duration = duration,
                watchDuration = watchDuration
            )}


            when(val response = getresponse.await())
            {
                is ApiResource.Failure -> Unit
                is ApiResource.Success -> {

                    if(response.value?.Success == true)
                        _quranLearningQuizLiveData.value = QuranLearningResource.QuranClassVideoWatched


                }
            }
        }
    }

    suspend fun submitQuizAnswer(answerSheet: String)
    {
        viewModelScope.launch {

            when(val response = quranLearningRepository.submitQuizAnswer(answerSheet))
            {
                is ApiResource.Failure -> _quranLearningQuizLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {

                    if(response.value?.Success == true)
                        _quranLearningQuizLiveData.value = QuranLearningResource.QuranQuizResult(response.value.Data)
                    else
                        _quranLearningQuizLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }


}