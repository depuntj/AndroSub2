package com.example.androsubmis2.models

data class ListResponse(
	val listEvents: List<EventModel?>? = null,
	val error: Boolean? = null,
	val message: String? = null
)
