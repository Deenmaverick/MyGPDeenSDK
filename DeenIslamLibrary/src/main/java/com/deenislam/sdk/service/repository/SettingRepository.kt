package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.database.dao.UserPrefDao
import com.deenislam.sdk.service.database.entity.UserPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SettingRepository(
    private val userPrefDao: UserPrefDao?
) {

    suspend fun updateSetting(language:String): UserPref? =
        withContext(Dispatchers.IO)
        {
            val data = userPrefDao?.select()

            data?.let {
                if (it.isNotEmpty()) {
                    it[0]?.language = language
                    if(userPrefDao?.update(it)!=0)
                    return@withContext userPrefDao?.select()?.get(0)
                }
            }
            return@withContext data?.get(0)
        }


    suspend fun getLanguage() =
        withContext(Dispatchers.IO)
        {
            val data = userPrefDao?.select()

            return@withContext data?.get(0)
        }
} 