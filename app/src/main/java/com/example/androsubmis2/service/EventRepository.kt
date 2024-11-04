package com.example.androsubmis2.service

import com.example.androsubmis2.models.DetailResponse
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.models.ListResponse
import retrofit2.Response

class EventRepository(private val eventService: EventService) {

    suspend fun getActiveEvents(): Response<ListResponse> {
        return eventService.getActiveEvents()
    }

    suspend fun getCompletedEvents(): Response<ListResponse> {
        return eventService.getCompletedEvents()
    }

    suspend fun getEventDetails(eventId: Int): Response<DetailResponse> {
        return eventService.getEventDetails(eventId)
    }
    suspend fun getNearestActiveEvent(): List<EventModel> {
        return eventService.getNearestActiveEvent()
    }
}
