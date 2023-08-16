package com.deenislam.sdk.service.libs.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlarmReceiverWork(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Do your background task here
        // For example, an API call, database operation, etc.
        val prayerDate = inputData.getString("prayerDate")
        val prayerTimesRepository = PrayerTimesRepository(
            deenService = NetworkProvider().getInstance().provideDeenService(),
            prayerNotificationDao = DatabaseProvider().getInstance()
                .providePrayerNotificationDao(),
            prayerTimesDao = null
        )

            if (prayerDate != null) {
                prayerTimesRepository.refill_prayer_notification_for_alarm_service(prayerDate)
            }


        // Return the result of your task
        return Result.success()
    }
}