package com.riteshkumar.backgroundlocationupdate

/*class MessageUploadWorker constructor(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher_foreground)
        .setContentTitle("Bank Support is running...")
        .setOngoing(true)

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            createNotificationChannel()
            val notification = notificationBuilder.build()
            setForeground(ForegroundInfo(NOTIFICATION_ID, notification))
            val message = inputData.getString(MESSAGE) ?: ""
            val sender = inputData.getString(SENDER) ?: ""
            val timeStamp = inputData.getString(TIME_IN_MILLIS) ?: ""
            val request = PostMessageRequestModel(
                data = MessageRequest(sender = sender, message = message, timeStamp = timeStamp)
            )

            val service = PostService.create()
            service.postMessageData(request)
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (notificationChannel == null) {
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        TAG,
                        NotificationManager.IMPORTANCE_LOW
                    )
                )
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "channelId"
        const val NOTIFICATION_ID = 1001
        const val TAG = "foregroundWorker"
    }
}*/
