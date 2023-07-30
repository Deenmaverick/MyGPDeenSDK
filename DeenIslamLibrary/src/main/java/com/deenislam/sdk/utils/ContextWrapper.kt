package com.deenislam.sdk.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object ContextWrapper {
    fun wrap(context: Context, newLocale: Locale): Context {
        var context = context
        val res = context.resources
        val configuration = Configuration(res.configuration) // Create a new configuration instance

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale)

            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)

            context = context.createConfigurationContext(configuration)

        } else {
            configuration.setLocale(newLocale)
            context = context.createConfigurationContext(configuration)
        }

        return context
    }
}

