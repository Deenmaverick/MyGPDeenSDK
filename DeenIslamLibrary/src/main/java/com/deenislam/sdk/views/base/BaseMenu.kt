package com.deenislam.sdk.views.base

import com.deenislam.sdk.R
import com.deenislam.sdk.service.models.MenuModel
import com.deenislam.sdk.utils.*

class BaseMenu {

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

    fun getDashboardMenu():ArrayList<MenuModel>
    {
        instance?.DashboardmenuList?.let {

            if(it.size <=0) {

                //Al Quran
                it.add(MenuModel(R.drawable.ic_menu_quran,"Al Quran", MENU_AL_QURAN))
                //Hajj & Umrah
                it.add(MenuModel(R.drawable.ic_menu_kabah,"Hajj & Umrah", MENU_HAJJ_AND_UMRAH))
                //Prayer time
                it.add(MenuModel(R.drawable.ic_menu_prayer,"Prayer Times", MENU_PRAYER_TIME))
                //Ramadan time
                it.add(MenuModel(R.drawable.ic_menu_calander,"Ramadan", MENU_RAMADAN))
                //Hadith
                it.add(MenuModel(R.drawable.ic_menu_hadith,"Hadith", MENU_HADITH))
                //Daily Dua
                it.add(MenuModel(R.drawable.ic_menu_dua,"Daily Dua", MENU_DUA))
                //99 Name of allah
                it.add(MenuModel(R.drawable.ic_menu_99_name_of_allah,"99 Names of Allah", MENU_99_NAME_OF_ALLAH))
                //Zakat
                it.add(MenuModel(R.drawable.ic_menu_zakat,"Zakat", MENU_ZAKAT))

            }
        }

        return instance?.DashboardmenuList?: arrayListOf()

    }


    fun getMoreMenu():ArrayList<MenuModel>
    {
        instance?.MoremenuList?.let {

            if(it.size <=0) {

                //Al Quran
                it.add(MenuModel(R.drawable.ic_menu_quran_2,"Al Quran", MENU_AL_QURAN))

                //Ramadan time
                it.add(MenuModel(R.drawable.ic_menu_ramadan,"Ramadan", MENU_RAMADAN))

                //Daily Dua
                it.add(MenuModel(R.drawable.ic_menu_dua_1,"Daily Dua", MENU_DUA))

                //Hadith
                it.add(MenuModel(R.drawable.ic_menu_islamic_book,"Hadith", MENU_HADITH))

                //Zakat
                it.add(MenuModel(R.drawable.ic_menu_zakat_2,"Zakat", MENU_ZAKAT))

                //Digital Tasbih
                it.add(MenuModel(R.drawable.ic_menu_digital_tasbih,"Digital Tasbeeh", MENU_DIGITAL_TASBEEH))

                //Nearest Mosque
                it.add(MenuModel(R.drawable.ic_menu_nearest_mosque,"Nearest Mosque", MENU_NEAREST_MOSQUE))

                //Prayer time
                it.add(MenuModel(R.drawable.ic_menu_mosque_2,"Prayer Times", MENU_PRAYER_TIME))

                //99 Name of allah
                it.add(MenuModel(R.drawable.ic_allahu,"99 Names of Allah", MENU_99_NAME_OF_ALLAH))

                //Hajj
                it.add(MenuModel(R.drawable.ic_menu_hajj,"Hajj", MENU_HAJJ))

                //Quran Class
                it.add(MenuModel(R.drawable.ic_menu_quran_class,"Quran Class", MENU_QURAN_CLASS))

                //Podcast
                it.add(MenuModel(R.drawable.ic_menu_podcast,"Podcast", MENU_PODCAST))


                //Hajj Package
                it.add(MenuModel(R.drawable.ic_menu_hajj_package,"Hajj Package", MENU_HAJJ_PACKAGE))

                //Prayer Learning
                it.add(MenuModel(R.drawable.ic_menu_namaz_rules,"Prayer Learning", MENU_PRAYER_LEARNING))

                //Eid
                it.add(MenuModel(R.drawable.ic_menu_eid_namaz,"EID", MENU_EID))

                //Islamic Calendar
                it.add(MenuModel(R.drawable.ic_menu_islamic_event,"Islamic Calendar", MENU_ISLAMIC_CALENDAR))

                //Live Video
                it.add(MenuModel(R.drawable.ic_menu_live_video_2,"Live Video", MENU_LIVE_VIDEO))

                //Islamic Jiggasa
                it.add(MenuModel(R.drawable.ic_menu_question_answer,"Islamic Jiggasa", MENU_ISLAMIC_JIGGASA))

                //Azan
                it.add(MenuModel(R.drawable.ic_menu_azan,"Azan", MENU_ISLAMIC_JIGGASA))

            }
        }


        return instance?.MoremenuList?: arrayListOf()

    }

}