package com.example.androsubmis2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.androsubmis2.databinding.ActivityMainBinding
import com.example.androsubmis2.fragments.ActiveEventsFragment
import com.example.androsubmis2.fragments.FavoriteEventsFragment
import com.example.androsubmis2.fragments.HomeFragment
import com.example.androsubmis2.fragments.PastEventsFragment
import com.example.androsubmis2.fragments.SettingEventsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
//                R.id.nav_favorite_events -> {
//                    loadFragment(FavoriteEventsFragment())
//                    true
//                }
//                R.id.nav_setting_events -> {
//                    loadFragment(SettingEventsFragment())
//                    true
//                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
