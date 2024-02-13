package com.deenislam.sdk.service

import android.content.Context
import android.content.SharedPreferences
import com.deenislam.sdk.service.models.UserLocation

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object AppPreference {

    private lateinit var mGSonInstance: Gson
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences
    private const val PREF_FILE_NAME = "DeenPreference"
    private const val USER_CURRENT_LOCATION = "userCurrentLocation"
    private const val USER_CURRENT_STATE = "userCurrentState"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, MODE)

        mGSonInstance = Gson()
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }
    fun saveUserCurrentLocation(location: UserLocation) {
        val userCurLocString = mGSonInstance.toJson(location)
        preferences.edit { it.putString(USER_CURRENT_LOCATION, userCurLocString) }
    }

    fun saveUserCurrentState(state: String) {
        preferences.edit { it.putString(USER_CURRENT_STATE, state) }
    }

    fun getUserCurrentState(): String? {
       return preferences.getString(USER_CURRENT_STATE, "Dhaka")
    }

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    fun getUserCurrentLocation(): UserLocation {
        val userCurLocString = preferences.getString(USER_CURRENT_LOCATION, "")

        if (userCurLocString == null || userCurLocString.length < 1) {
            val mLocation = UserLocation(
                23.8103,
                90.4125
            )
            return mLocation
        }
        val type: Type = genericType<UserLocation>()
        return mGSonInstance.fromJson(userCurLocString, type)
    }
}