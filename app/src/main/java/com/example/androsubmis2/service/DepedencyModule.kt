package com.example.androsubmis2.service

import com.example.androsubmis2.setting.ThemePreferenceManager
import com.example.androsubmis2.models.FavoriteDatabase
import com.example.androsubmis2.view.ViewEventModel
import com.example.androsubmis2.view.ViewFavoriteModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { EventRepository(RetrofitInstance.eventService) }
    single { FavoriteDatabase.getDatabase(androidContext()).favoriteEventDao() }
    single { FavoriteDatabase.getDatabase(get()).favoriteEventDao() }
    single { ThemePreferenceManager(androidContext()) }

    viewModel { ViewEventModel(get()) }
    viewModel { ViewFavoriteModel(get()) }
}
