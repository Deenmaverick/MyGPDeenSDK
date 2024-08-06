package com.deenislamic.sdk.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class FileDownloader(private val context: Context) {

    private var downloadJob: Job? = null



    suspend fun downloadFile(url: String, ext: String,progressListener: ProgressListener? = null): Result<File> = suspendCoroutine { continuation ->
        //cancelDownload()
        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    continuation.resumeWith(Result.failure(Exception("Server returned HTTP ${connection.responseCode} ${connection.responseMessage}")))
                    return@launch
                }

                Log.e("FileDownloader","${connection.contentLength} $url")


                val fileSize = connection.contentLength
                val file = File(context.cacheDir, "quran_learning$ext")
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var bytesRead: Int
                var totalBytesRead = 0

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    if (!isActive) {
                        inputStream.close()
                        outputStream.close()
                        file.delete()
                        continuation.resumeWith(Result.failure(CancellationException("Download cancelled")))
                        return@launch
                    }
                    outputStream.write(buffer, 0, bytesRead)

                    totalBytesRead += bytesRead

                    // Calculate and report progress
                    val progress = (totalBytesRead * 100 / fileSize)
                    progressListener?.onProgressUpdate(progress)
                }

                outputStream.close()
                inputStream.close()
                connection.disconnect()

                continuation.resume(Result.success(file))
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }

        downloadJob?.start()
    }

    fun cancelDownload() {
        downloadJob?.cancel()
    }

    interface ProgressListener {
        fun onProgressUpdate(progress: Int)
    }
}

