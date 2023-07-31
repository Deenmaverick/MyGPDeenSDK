package com.deenislam.sdk.views.base

import android.content.Context
import com.deenislam.sdk.R
import com.deenislam.sdk.service.models.MenuModel
import com.deenislam.sdk.utils.*

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

    fun getMenuArray():ArrayList<MenuModel>
    {
        if(menuList.size <=0) {

            //Hajj & Umrah
            menuList.add(MenuModel(R.drawable.ic_menu_kabah,"Hajj & Umrah", MENU_HAJJ_AND_UMRAH))
            //Prayer time
            menuList.add(MenuModel(R.drawable.ic_menu_prayer,"Prayer time", MENU_PRAYER_TIME))
            //Al Quran
            menuList.add(MenuModel(R.drawable.ic_menu_quran,"Al Quran", MENU_AL_QURAN))
            //Hadith
            menuList.add(MenuModel(R.drawable.ic_menu_hadith,"Hadith", MENU_HADITH))
            //Ramadan time
            menuList.add(MenuModel(R.drawable.ic_menu_calander,"Ramadan time", MENU_RAMADAN))
            //Inspiration
            menuList.add(MenuModel(R.drawable.ic_menu_inspiration,"Inspiration", MENU_INSPIRATION))
            //Dua
            menuList.add(MenuModel(R.drawable.ic_menu_dua,"Dua", MENU_DUA))
            //Learning
            menuList.add(MenuModel(R.drawable.ic_menu_learning,"Learning", MENU_LEARNING))
            //Islamic goods
            menuList.add(MenuModel(R.drawable.ic_menu_goods,"Islamic goods", MENU_ISLAMIC_GOODS))
            //Islamic content
            menuList.add(MenuModel(R.drawable.ic_menu_content,"Islamic content", MENU_ISALIC_CONTENT))
            //Islamic lifestyle
            menuList.add(MenuModel(R.drawable.ic_menu_lifestyle,"Islamic lifestyle",
                MENU_ISLAMIC_LIFESTYLE))
            //Donation
            menuList.add(MenuModel(R.drawable.ic_menu_donation,"Donation", MENU_DONATION))
        }

        return menuList

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


    fun getMoreMenu():ArrayList<MenuModel>
    {
        instance?.MoremenuList?.let {

            if(it.size <=0) {

                //Al Quran
                it.add(MenuModel(R.drawable.ic_menu_quran,"Al Quran", MENU_AL_QURAN))

                //Ramadan time
                it.add(MenuModel(R.drawable.ic_menu_kabah,"Hajj & Umrah", MENU_HAJJ_AND_UMRAH))

                //Prayer time
                it.add(MenuModel(R.drawable.ic_menu_prayer,"Prayer Times", MENU_PRAYER_TIME))

                //Daily Dua
                it.add(MenuModel(R.drawable.ic_ramadan,"Ramadan", MENU_RAMADAN))

                //Hadith
                it.add(MenuModel(R.drawable.ic_menu_hadith,"Hadith", MENU_HADITH))

                //Daily Dua
                it.add(MenuModel(R.drawable.ic_menu_dua,"Daily Dua", MENU_DUA))

                //99 Name of allah
                it.add(MenuModel(R.drawable.ic_menu_99_name_of_allah,"99 Names of Allah", MENU_99_NAME_OF_ALLAH))

                //Zakat
                it.add(MenuModel(R.drawable.ic_menu_zakat,"Zakat", MENU_ZAKAT))

                //Digital Tasbih
                it.add(MenuModel(R.drawable.ic_menu_digital_tasbeeh,"Digital Tasbeeh", MENU_DIGITAL_TASBEEH))

                //Prayer Learning
                it.add(MenuModel(R.drawable.ic_menu_learning,"Prayer Learning", MENU_LEARNING))

                //Islamic Event
                it.add(MenuModel(R.drawable.ic_menu_islamic_event,"Islamic Event", MENU_ISLAMIC_EVENT))

                //Islamic Name
                it.add(MenuModel(R.drawable.ic_menu_islamic_name,"Islamic Name", MENU_ISLAMIC_NAME))


            }
        }


        return instance?.MoremenuList?: arrayListOf()

    }

}