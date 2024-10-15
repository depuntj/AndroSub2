package com.example.androsubmis2.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.models.DetailResponse
import com.example.androsubmis2.models.ListResponse
import com.example.androsubmis2.service.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class ViewEventModel : ViewModel() {

    private val eventService = RetrofitInstance.eventService

    private val _activeEventsLiveData = MutableLiveData<List<EventModel>>()
    val activeEventsLiveData: LiveData<List<EventModel>> get() = _activeEventsLiveData

    private val _completedEventsLiveData = MutableLiveData<List<EventModel>>()
    val completedEventsLiveData: LiveData<List<EventModel>> get() = _completedEventsLiveData

    private val _searchResultsLiveData = MutableLiveData<List<EventModel>>()
    val searchResultsLiveData: LiveData<List<EventModel>> get() = _searchResultsLiveData

    private val _eventDetailLiveData = MutableLiveData<EventModel?>()
    val eventDetailLiveData: LiveData<EventModel?> get() = _eventDetailLiveData

    private val _errorMessageLiveData = MutableLiveData<String?>()
    val errorMessageLiveData: LiveData<String?> get() = _errorMessageLiveData

    fun fetchActiveEvents() {
        viewModelScope.launch {
            try {
                val response: Response<ListResponse> = eventService.getActiveEvents()
                if (response.isSuccessful && response.body()?.listEvents != null) {
                    _activeEventsLiveData.value = response.body()?.listEvents?.filterNotNull()
                    Log.d("ViewEventModel", "Active events fetched: ${response.body()?.listEvents?.size} events")
                } else {
                    _errorMessageLiveData.value = response.body()?.message ?: "Unknown error"
                    Log.d("ViewEventModel", "Error: ${response.body()?.message}")
                }
            } catch (e: HttpException) {
                _errorMessageLiveData.value = "Failed to load active events: ${e.message()}"
                Log.e("ViewEventModel", "Failed to load active events: ${e.message()}")
            } catch (e: Exception) {
                _errorMessageLiveData.value = e.message
                Log.e("ViewEventModel", "Failed to load active events: ${e.message}")
            }
        }
    }

    fun fetchCompletedEvents() {
        viewModelScope.launch {
            try {
                val response: Response<ListResponse> = eventService.getCompletedEvents()
                if (response.isSuccessful && response.body()?.listEvents != null) {
                    _completedEventsLiveData.value = response.body()?.listEvents?.filterNotNull()
                    Log.d("ViewEventModel", "Completed events fetched: ${response.body()?.listEvents?.size} events")
                } else {
                    _errorMessageLiveData.value = response.body()?.message ?: "Unknown error"
                    Log.d("ViewEventModel", "Error: ${response.body()?.message}")
                }
            } catch (e: HttpException) {
                _errorMessageLiveData.value = "Failed to load completed events: ${e.message()}"
                Log.e("ViewEventModel", "Failed to load completed events: ${e.message()}")
            } catch (e: Exception) {
                _errorMessageLiveData.value = e.message
                Log.e("ViewEventModel", "Failed to load completed events: ${e.message}")
            }
        }
    }

    fun searchEvents(keyword: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val activeStatus = if (isActive) 1 else 0
                val response: Response<ListResponse> = eventService.searchEvents(active = activeStatus, keyword = keyword)

                if (response.isSuccessful && response.body()?.listEvents != null) {
                    val events = response.body()?.listEvents?.filterNotNull() ?: emptyList()
                    _searchResultsLiveData.value = events.ifEmpty {
                        emptyList()
                    }
                } else {
                    _errorMessageLiveData.value = response.body()?.message ?: "No events found."
                }
            } catch (e: HttpException) {
                _errorMessageLiveData.value = "Failed to search events: ${e.message()}"
            } catch (e: Exception) {
                _errorMessageLiveData.value = e.message ?: "An unknown error occurred"
            }
        }
    }

    fun fetchEventDetail(eventId: Int) {
        viewModelScope.launch {
            try {
                val response: Response<DetailResponse> = eventService.getEventDetails(eventId)
                if (response.isSuccessful && response.body()?.event != null) {
                    _eventDetailLiveData.value = response.body()?.event
                    Log.d("ViewEventModel", "Event details fetched: ${response.body()?.event?.name}")
                } else {
                    _errorMessageLiveData.value = response.body()?.message ?: "Unknown error"
                    Log.d("ViewEventModel", "Error: ${response.body()?.message}")
                }
            } catch (e: HttpException) {
                _errorMessageLiveData.value = "Failed to fetch event detail: ${e.message()}"
                Log.e("ViewEventModel", "Failed to fetch event detail: ${e.message()}")
            } catch (e: Exception) {
                _errorMessageLiveData.value = e.message
                Log.e("ViewEventModel", "Failed to fetch event detail: ${e.message}")
            }
        }
    }
}
