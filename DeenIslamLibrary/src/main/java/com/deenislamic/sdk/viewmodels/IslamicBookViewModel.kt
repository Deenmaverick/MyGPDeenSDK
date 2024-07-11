package com.deenislamic.sdk.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicBookResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.boyan.categoriespaging.BoyanCategoriesResponse
import com.deenislamic.sdk.service.network.response.boyan.scholarspaging.BoyanScholarResponse
import com.deenislamic.sdk.service.network.response.islamicbook.IslamicBookResponse
import com.deenislamic.sdk.service.repository.IslamicBookRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import kotlinx.coroutines.launch


internal class IslamicBookViewModel(
    private val repository: IslamicBookRepository,
    private val quranLearningRepository: QuranLearningRepository
) : ViewModel() {

    private val _islamicBookHomeLiveData: MutableLiveData<IslamicBookResource> = MutableLiveData()
    val islamicBookHomeLiveData: MutableLiveData<IslamicBookResource> get() = _islamicBookHomeLiveData

    private val _bookAuthirsLiveData: MutableLiveData<IslamicBookResource> = MutableLiveData()
    val bookAuthirsLiveData: MutableLiveData<IslamicBookResource> get() = _bookAuthirsLiveData

    private val _bookCategoriesLiveData: MutableLiveData<IslamicBookResource> = MutableLiveData()
    val bookCategoriesLiveData: MutableLiveData<IslamicBookResource> get() = _bookCategoriesLiveData

    private val _bookByAuthorLiveData: MutableLiveData<IslamicBookResource> = MutableLiveData()
    val bookByAuthorLiveData: MutableLiveData<IslamicBookResource> get() = _bookByAuthorLiveData

    private val _bookByCategoryLiveData: MutableLiveData<IslamicBookResource> = MutableLiveData()
    val bookByCategoryLiveData: MutableLiveData<IslamicBookResource> get() = _bookByCategoryLiveData

    private val _secureUrlLiveData:MutableLiveData<IslamicBookResource> = MutableLiveData()
    val secureUrlLiveData:MutableLiveData<IslamicBookResource> get() = _secureUrlLiveData

    private val _favoriteBookLiveData:MutableLiveData<IslamicBookResource> = MutableLiveData()
    val favoriteBookLiveData:MutableLiveData<IslamicBookResource> get() = _favoriteBookLiveData

    private val _favoritedIslamicBooksLiveData:MutableLiveData<IslamicBookResource> = MutableLiveData()
    val favoritedIslamicBooksLiveData:MutableLiveData<IslamicBookResource> get() = _favoritedIslamicBooksLiveData

    fun getIslamicBookHome(language: String) {
        viewModelScope.launch {

            when (val response = repository.getIslamicBookHome(language = language)) {
                is ApiResource.Failure -> _islamicBookHomeLiveData.value =
                    CommonResource.API_CALL_FAILED

                is ApiResource.Success -> {
                    if (response.value?.Success == true)
                        _islamicBookHomeLiveData.value = IslamicBookResource.IslamicBookHomeData(response.value.Data)
                    else
                        _islamicBookHomeLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getBookAuthors(language:String, page:Int, limit:Int){
        viewModelScope.launch {
            processBookAuthorsResponse(
                repository.getBookAuthors(
                    language = language,
                    page = page,
                    limit = limit
                ) as ApiResource<BoyanScholarResponse>
            )
        }
    }

    private fun processBookAuthorsResponse(response: ApiResource<BoyanScholarResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _bookAuthirsLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _bookAuthirsLiveData.value = IslamicBookResource.BookAuthorsData(response.value.Data)
                else
                    _bookAuthirsLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun getBookCategory(language:String, page:Int, limit:Int){
        viewModelScope.launch {
            processBookCategoryResponse(
                repository.getBookCategory(
                    language = language,
                    page = page,
                    limit = limit
                ) as ApiResource<BoyanCategoriesResponse>
            )
        }
    }

    private fun processBookCategoryResponse(response: ApiResource<BoyanCategoriesResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _bookCategoriesLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _bookCategoriesLiveData.value = IslamicBookResource.BookCategoryData(response.value.Data)
                else
                    _bookCategoriesLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun getBookItemByAuthors(Id: Int, page:Int, limit:Int){
        viewModelScope.launch {
            processGetBookItemByAuthorsResponse(
                repository.getBookItemByAuthors(
                    Id = Id,
                    page = page,
                    limit = limit
                ) as ApiResource<IslamicBookResponse>
            )
        }
    }

    private fun processGetBookItemByAuthorsResponse(response: ApiResource<IslamicBookResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _bookByAuthorLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _bookByAuthorLiveData.value = IslamicBookResource.BookItemData(response.value.Data)
                else
                    _bookByAuthorLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun getBookItemByCategory(Id: Int, page:Int, limit:Int){
        viewModelScope.launch {
            processGetBookItemByCategoryResponse(
                repository.getBookItemByCategory(
                    Id = Id,
                    page = page,
                    limit = limit
                ) as ApiResource<IslamicBookResponse>
            )
        }
    }

    private fun processGetBookItemByCategoryResponse(response: ApiResource<IslamicBookResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _bookByCategoryLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _bookByCategoryLiveData.value = IslamicBookResource.BookItemData(response.value.Data)
                else
                    _bookByCategoryLiveData.value = CommonResource.EMPTY
            }
        }
    }

    suspend fun getDigitalQuranSecureUrl(url: String, forDownload: Boolean, bookId: Int, bookTitle: String)
    {
        viewModelScope.launch {

            when(val response = quranLearningRepository.getDigitalQuranSecureUrl(url))
            {
                is ApiResource.Failure -> Unit
                is ApiResource.Success ->{
                    if(response.value?.url?.isNotEmpty() == true)
                        _secureUrlLiveData.value = IslamicBookResource.PdfSecureUrl(response.value.url, forDownload, bookId, bookTitle)
                    else Unit
                }
            }

        }
    }

    fun makeBookFavorite(ContentId: String, isFavorite:Boolean, language:String) {
        viewModelScope.launch {

            when (val response = repository.makeBookFavorite(ContentId = ContentId, isFavorite = isFavorite, language = language)) {
                is ApiResource.Failure -> _favoriteBookLiveData.value =
                    CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (response.value?.Success == true)
                        _favoriteBookLiveData.value = IslamicBookResource.FavoriteBookItemData(response.value)
                    else
                        _favoriteBookLiveData.value = CommonResource.EMPTY
                }
            }
        }
    }

    fun getFavouriteBooks(language: String, page:Int, limit:Int){
        viewModelScope.launch {
            processGetFavoriteBookResponse(
                repository.getFavouriteBooks(
                    language = language,
                    page = page,
                    limit = limit
                ) as ApiResource<IslamicBookResponse>
            )
        }
    }

    private fun processGetFavoriteBookResponse(response: ApiResource<IslamicBookResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _favoritedIslamicBooksLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data?.isNotEmpty() == true)
                    _favoritedIslamicBooksLiveData.value = IslamicBookResource.BookItemData(response.value.Data)
                else
                    _favoritedIslamicBooksLiveData.value = CommonResource.EMPTY
            }
        }
    }

    fun clearDownloader(){
        _secureUrlLiveData.value = CommonResource.CLEAR
    }

}