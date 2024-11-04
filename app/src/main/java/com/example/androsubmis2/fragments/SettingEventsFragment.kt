package com.example.androsubmis2.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androsubmis2.R
import com.example.androsubmis2.databinding.FragmentSettingBinding
import com.example.androsubmis2.setting.ThemePreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingEventsFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val themePreferenceManager: ThemePreferenceManager by inject()

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            sendNotificationEnabledConfirmation()
        } else {
            Toast.makeText(
                requireContext(),
                "Notification permission is required to show notifications",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root

        lifecycleScope.launch {
            val isDarkModeEnabled = themePreferenceManager.isDarkModeEnabled.first()
            binding.switchDarkMode.isChecked = isDarkModeEnabled
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                themePreferenceManager.setDarkModeEnabled(isChecked)
                applyTheme(isChecked)
            }
        }

        lifecycleScope.launch {
            val isNotificationEnabled = themePreferenceManager.isNotificationEnabled.first()
            binding.switchNotification.isChecked = isNotificationEnabled
        }

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                themePreferenceManager.setNotificationEnabled(isChecked)
                if (isChecked) {
                    requestNotificationPermissionIfNeeded()
                }
            }
        }

        return view
    }

    private fun applyTheme(isDarkModeEnabled: Boolean) {
        val nightMode = if (isDarkModeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        sendNotificationEnabledConfirmation()
    }

    private fun sendNotificationEnabledConfirmation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "notification_settings_channel",
                "Notification Settings",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for notification settings confirmation"
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(requireContext(), "notification_settings_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notifications Enabled")
            .setContentText("You have enabled event notifications.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(requireContext()).notify(1001, notification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
