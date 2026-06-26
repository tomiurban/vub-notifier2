package com.tomiu.vubnotifier

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tomiu.vubnotifier.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PREFS = "vub_notifier_prefs"
        private const val KEY_SERVER_URL = "server_url"
        private const val DEFAULT_URL = "http://100.79.107.128:8899/api/vub-notification"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)

        binding.etServerUrl.setText(
            prefs.getString(KEY_SERVER_URL, DEFAULT_URL)
        )

        binding.btnSave.setOnClickListener {
            val url = binding.etServerUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(this, "Zadaj URL servera", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.edit().putString(KEY_SERVER_URL, url).apply()
            Toast.makeText(this, "URL uložená ✓", Toast.LENGTH_SHORT).show()
        }

        binding.btnPermission.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        val enabled = isNotificationListenerEnabled()
        if (enabled) {
            binding.tvStatus.text = "✅ Povolenie aktívne — VÚB notifikácie sa posielajú"
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            binding.btnPermission.text = "Skontrolovať povolenie"
        } else {
            binding.tvStatus.text = "⚠️ Povolenie nie je udelené — klikni na tlačidlo nižšie"
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_orange_dark))
            binding.btnPermission.text = "Udeliť povolenie"
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val flat = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        val cn = ComponentName(this, VubNotificationService::class.java)
        return flat.contains(cn.flattenToString())
    }
}
