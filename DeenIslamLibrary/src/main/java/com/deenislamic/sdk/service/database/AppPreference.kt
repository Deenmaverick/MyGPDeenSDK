package com.deenislamic.sdk.service.database

import android.content.Context
import android.content.SharedPreferences
import com.deenislamic.sdk.service.models.UserLocation
import com.deenislamic.sdk.service.models.common.ContentSetting
import com.deenislamic.sdk.service.models.common.PdfSetting
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

internal object AppPreference {

    private lateinit var mGSonInstance: Gson
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences
    private const val PREF_FILE_NAME = "DeenPreference"
    private const val USER_CURRENT_LOCATION = "userCurrentLocation"
    private const val USER_CURRENT_STATE = "userCurrentState"
    private const val CONTENT_SETTING = "ContentSetting"
    private const val PRAYER_TIME_LOC = "PrayerTimeLoc"
    private const val PDF_SETTING = "PdfSetting"

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

    fun setContentSetting(contentSetting: ContentSetting){
        preferences.edit {
            it.putString(CONTENT_SETTING,Gson().toJson(contentSetting))
        }
    }

    fun getContentSetting(): ContentSetting {
        val jsonString = preferences.getString(CONTENT_SETTING, null)
        return if (jsonString != null) {
            Gson().fromJson(jsonString, ContentSetting::class.java)
        } else {
            // Return a default value if the JSON string is null
            ContentSetting()
        }
    }

    fun savePrayerTimeLoc(state: String) {
        preferences.edit { it.putString(PRAYER_TIME_LOC, state) }
    }

    fun getPrayerTimeLoc(): String? {
        return preferences.getString(PRAYER_TIME_LOC, "Dhaka")
    }

    fun getPdfSetting(): PdfSetting {
        val jsonString = preferences.getString(PDF_SETTING, null)
        return if (jsonString != null) {
            Gson().fromJson(jsonString, PdfSetting::class.java)
        } else {
            // Return a default value if the JSON string is null
            PdfSetting()
        }
    }

    fun setPdfSetting(pdfSetting: PdfSetting){
        preferences.edit {
            it.putString(PDF_SETTING,Gson().toJson(pdfSetting))
        }
    }
}