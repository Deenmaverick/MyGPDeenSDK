package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.network.response.dashboard.Data

internal interface IslamicBookResource {

    data class IslamicBookHomeData(val data: List<Data>) : IslamicBookResource

    data class BookAuthorsData(val data: List<com.deenislamic.sdk.service.network.response.boyan.scholarspaging.Data>): IslamicBookResource

    data class BookCategoryData(val data: List<com.deenislamic.sdk.service.network.response.boyan.categoriespaging.Data>): IslamicBookResource

    data class BookItemData(val data: List<com.deenislamic.sdk.service.network.response.islamicbook.Data>): IslamicBookResource

    data class PdfSecureUrl(val url: String, val forDownload: Boolean, val bookId: Int, val bookTitle: String) : IslamicBookResource

    data class FavoriteBookItemData(val data: com.deenislamic.sdk.service.network.response.islamicbook.favorite.FavoriteBookResponse): IslamicBookResource

}