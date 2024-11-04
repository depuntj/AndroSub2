package com.example.androsubmis2.service

import com.example.androsubmis2.view.ViewEventModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { EventRepository(RetrofitInstance.eventService) }

    viewModel { ViewEventModel(get()) }
}
