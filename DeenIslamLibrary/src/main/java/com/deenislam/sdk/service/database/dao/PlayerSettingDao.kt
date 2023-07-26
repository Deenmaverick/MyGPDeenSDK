package com.deenislam.sdk.service.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.deenislam.sdk.service.database.entity.PlayerSettingPref

@Dao
internal abstract class PlayerSettingDao: BaseDao<PlayerSettingPref> {

    @Query("SELECT * from playersettingpref where id='1'")
    abstract fun select():List<PlayerSettingPref?>


}