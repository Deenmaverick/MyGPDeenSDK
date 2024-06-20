package com.deenislamic.sdk.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.view.ContextThemeWrapper
import java.util.Locale

object LocaleUtil {


        fun createLocaleContext(context: Context, locale: Locale): Context {
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)

            val localContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.createConfigurationContext(config)
            } else {
                @Suppress("DEPRECATION")
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
                context
            }

            val wrappedContext = ContextThemeWrapper(localContext, context.theme)

            return wrappedContext

        }


}