package com.deenislamic.sdk.service.database.dao

import androidx.room.*
import com.deenislamic.sdk.service.database.entity.UserPref

@Dao
internal abstract class UserPrefDao: BaseDao<UserPref> {

    @Query("SELECT * from userpref where id='1'")
    abstract fun select():List<UserPref?>

    @Query("DELETE from userpref where id='1'")
    abstract fun delete():Int


}