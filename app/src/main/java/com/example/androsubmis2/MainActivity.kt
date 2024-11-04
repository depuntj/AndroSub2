package com.example.androsubmis2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androsubmis2.databinding.ActivityMainBinding
import com.example.androsubmis2.setting.ThemePreferenceManager
import com.example.androsubmis2.setting.scheduleReminder
import com.example.androsubmis2.fragments.ActiveEventsFragment
import com.example.androsubmis2.fragments.FavoriteEventsFragment
import com.example.androsubmis2.fragments.HomeFragment
import com.example.androsubmis2.fragments.PastEventsFragment
import com.example.androsubmis2.fragments.SettingEventsFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val themePreferenceManager: ThemePreferenceManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadThemeAndScheduleNotification()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_active_events -> {
                    loadFragment(ActiveEventsFragment())
                    true
                }
                R.id.nav_past_events -> {
                    loadFragment(PastEventsFragment())
                    true
                }
                R.id.nav_favorite_events -> {
                    loadFragment(FavoriteEventsFragment())
                    true
                }
                R.id.nav_setting_events -> {
                    loadFragment(SettingEventsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun loadThemeAndScheduleNotification() {
        lifecycleScope.launch {
            themePreferenceManager.isDarkModeEnabled.collect { isDarkModeEnabled ->
                val nightMode = if (isDarkModeEnabled) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(nightMode)
            }

            themePreferenceManager.isNotificationEnabled.collect { isNotificationEnabled ->
                if (isNotificationEnabled) {
                    scheduleReminder(this@MainActivity)
                }
            }
        }
    }
}
