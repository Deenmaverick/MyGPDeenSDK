package com.deenislamic.sdk.service.libs.notification

/*
internal class AlarmReceiverWork(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Do your background task here
        // For example, an API call, database operation, etc.
        val pid = inputData.getInt("pid",0)
        val prayerDate = inputData.getString("prayerDate")

        clear_prayer_notification_by_id(pid)

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

    private suspend fun clear_prayer_notification_by_id(pid:Int) =

        withContext(Dispatchers.IO)
        {
            val prayerNotificationDao = DatabaseProvider().getInstance().providePrayerNotificationDao()

            prayerNotificationDao?.clearNotificationByID(pid)

        }
}*/
