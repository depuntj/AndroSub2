package com.example.androsubmis2.service

import com.example.androsubmis2.models.DetailResponse
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.models.ListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventService {

    @GET("events")
    suspend fun getActiveEvents(
        @Query("active") active: Int = 1
    ): Response<ListResponse>

    @GET("events")
    suspend fun getCompletedEvents(
        @Query("active") active: Int = 0
    ): Response<ListResponse>

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") keyword: String? = null
    ): Response<ListResponse>

    @GET("events/{id}")
    suspend fun getEventDetails(
        @Path("id") eventId: Int
    ): Response<DetailResponse>

    @GET("events")
    suspend fun getNearestActiveEvent(
        @Query("active") active: Int = 1,
        @Query("limit") limit: Int = 1
    ): List<EventModel>
}
