package com.deenislam.sdk.service.database.dao

import androidx.room.*
import com.deenislam.sdk.service.database.entity.FavoriteMenu

@Dao
internal abstract class FavMenuDao: BaseDao<FavoriteMenu> {

    @Query("select * from fav_menu")
    abstract fun select_all():List<FavoriteMenu>

}