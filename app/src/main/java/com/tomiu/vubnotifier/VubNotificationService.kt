package com.tomiu.vubnotifier

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class VubNotificationService : NotificationListenerService() {

    companion object {
        private const val TAG = "VubNotifier"
        private const val VUB_PACKAGE = "sk.vub.mobilebanking"
        private const val PREFS = "vub_notifier_prefs"
        private const val KEY_SERVER_URL = "server_url"
        private const val DEFAULT_URL = "http://100.79.107.128:8899/api/vub-notification"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != VUB_PACKAGE) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val title = extras.getString("android.title") ?: return
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val bigText = extras.getCharSequence("android.bigText")?.toString() ?: text

        Log.d(TAG, "VÚB notifikácia: title=$title, text=$bigText")

        sendToServer(title, bigText)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Nič nerobíme pri odstránení notifikácie
    }

    private fun sendToServer(title: String, text: String) {
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val serverUrl = prefs.getString(KEY_SERVER_URL, DEFAULT_URL) ?: DEFAULT_URL

        val json = JSONObject().apply {
            put("title", title)
            put("text", text)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(serverUrl)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Chyba odosielania: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        Log.d(TAG, "Úspešne odoslané: ${it.code}")
                    } else {
                        Log.e(TAG, "Server error: ${it.code} ${it.body?.string()}")
                    }
                }
            }
        })
    }
}
