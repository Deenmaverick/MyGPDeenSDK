package com.deenislam.sdk.views.base

import android.content.Context
import com.deenislam.sdk.R
import com.deenislam.sdk.service.models.MenuModel
import com.deenislam.sdk.utils.MENU_AL_QURAN
import com.deenislam.sdk.utils.MENU_DIGITAL_TASBEEH
import com.deenislam.sdk.utils.MENU_DUA
import com.deenislam.sdk.utils.MENU_HADITH
import com.deenislam.sdk.utils.MENU_ISLAMIC_NAME
import com.deenislam.sdk.utils.MENU_PRAYER_TIME
import com.deenislam.sdk.utils.MENU_QIBLA_COMPASS
import com.deenislam.sdk.utils.MENU_ZAKAT

internal class BaseMenu {

    companion object
    {
        var instance:BaseMenu ? =null
    }

    private var menuList:ArrayList<MenuModel> = arrayListOf()
    private var DashboardmenuList:ArrayList<MenuModel> = arrayListOf()
    private var MoremenuList:ArrayList<MenuModel> = arrayListOf()

    fun getInstance():BaseMenu
    {
        if(instance == null)
            instance = BaseMenu()

        return instance as BaseMenu
    }


    fun getDashboardMenu(context: Context):ArrayList<MenuModel>
    {
        instance?.DashboardmenuList?.let {

            if(it.size <=0) {

                //Al Quran
                it.add(MenuModel(R.drawable.ic_menu_quran,context.resources.getString(R.string.al_quran), MENU_AL_QURAN))
                //Hadith
                it.add(MenuModel(R.drawable.ic_menu_hadith,context.resources.getString(R.string.hadith), MENU_HADITH))
                //Prayer time
                it.add(MenuModel(R.drawable.ic_menu_prayer,context.resources.getString(R.string.prayer_times), MENU_PRAYER_TIME))
                //Daily Dua
                it.add(MenuModel(R.drawable.ic_menu_dua,context.resources.getString(R.string.daily_dua), MENU_DUA))
                //Zakat
                it.add(MenuModel(R.drawable.ic_menu_zakat,context.resources.getString(R.string.zakat), MENU_ZAKAT))
                //Digital Tasbih
                it.add(MenuModel(R.drawable.ic_menu_digital_tasbeeh,context.resources.getString(R.string.digital_tasbeeh), MENU_DIGITAL_TASBEEH))
                //Qibla Compass
                it.add(MenuModel(R.drawable.ic_menu_compass,context.resources.getString(R.string.qibla_compass), MENU_QIBLA_COMPASS))
                //Islamic Name
                it.add(MenuModel(R.drawable.ic_menu_islamic_name,context.resources.getString(R.string.islamic_name), MENU_ISLAMIC_NAME))

            }
        }

        return instance?.DashboardmenuList?: arrayListOf()

    }


}