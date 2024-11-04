package com.example.androsubmis2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androsubmis2.DetailActivity
import com.example.androsubmis2.R
import com.example.androsubmis2.adapters.EventAdapter
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.models.FavoriteEventEntity
import com.example.androsubmis2.view.ViewEventModel
import com.example.androsubmis2.view.ViewFavoriteModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel: ViewEventModel by viewModel()
    private val viewFavoriteModel: ViewFavoriteModel by viewModel()

    private lateinit var recyclerActiveEvents: RecyclerView
    private lateinit var recyclerCompletedEvents: RecyclerView
    private lateinit var eventAdapterActive: EventAdapter
    private lateinit var eventAdapterCompleted: EventAdapter
    private lateinit var errorMessageTextView: TextView
    private lateinit var loadingIndicatorActive: ProgressBar
    private lateinit var loadingIndicatorCompleted: ProgressBar

    private var activeEvents: List<EventModel> = emptyList()
    private var completedEvents: List<EventModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerActiveEvents = view.findViewById(R.id.recycler_active_events)
        recyclerCompletedEvents = view.findViewById(R.id.recycler_completed_events)
        errorMessageTextView = view.findViewById(R.id.error_message)
        loadingIndicatorActive = view.findViewById(R.id.loading_indicator_active)
        loadingIndicatorCompleted = view.findViewById(R.id.loading_indicator_completed)

        recyclerActiveEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerCompletedEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        setupObservers()

        viewModel.fetchActiveEvents()
        viewModel.fetchCompletedEvents()

        return view
    }

    private fun setupObservers() {
        // Observe Active Events
        loadingIndicatorActive.visibility = View.VISIBLE
        viewModel.activeEventsLiveData.observe(viewLifecycleOwner) { events ->
            loadingIndicatorActive.visibility = View.GONE
            if (!events.isNullOrEmpty()) {
                errorMessageTextView.visibility = View.GONE
                activeEvents = events.take(5)
                eventAdapterActive = EventAdapter(
                    activeEvents,
                    { event -> onEventClicked(event) },
                    { event -> onFavoriteClicked(event, true) }
                )
                recyclerActiveEvents.adapter = eventAdapterActive
            } else {
                showErrorMessage("No active events available.")
            }
        }

        // Observe Completed Events
        loadingIndicatorCompleted.visibility = View.VISIBLE
        viewModel.completedEventsLiveData.observe(viewLifecycleOwner) { events ->
            loadingIndicatorCompleted.visibility = View.GONE
            if (!events.isNullOrEmpty()) {
                errorMessageTextView.visibility = View.GONE
                completedEvents = events.take(5)
                eventAdapterCompleted = EventAdapter(
                    completedEvents,
                    { event -> onEventClicked(event) },
                    { event -> onFavoriteClicked(event, false) }
                )
                recyclerCompletedEvents.adapter = eventAdapterCompleted
            } else {
                showErrorMessage("No completed events available.")
            }
        }
    }

    private fun onEventClicked(event: EventModel) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra("event_id", event.id)
        }
        startActivity(intent)
    }

    private fun onFavoriteClicked(event: EventModel, isActiveEvent: Boolean) {
        if (event.isFavorite) {
            viewFavoriteModel.removeFavorite(event.toFavoriteEntity())
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
        } else {
            viewFavoriteModel.addFavorite(event.toFavoriteEntity())
            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
        }
        event.isFavorite = !event.isFavorite

        if (isActiveEvent) {
            val index = activeEvents.indexOf(event)
            eventAdapterActive.notifyItemChanged(index)
        } else {
            val index = completedEvents.indexOf(event)
            eventAdapterCompleted.notifyItemChanged(index)
        }
    }

    private fun EventModel.toFavoriteEntity() = FavoriteEventEntity(
        id = this.id.toString(),
        name = this.name ?: "Unknown Event",
        mediaCover = this.imageLogo
    )

    private fun showErrorMessage(message: String) {
        errorMessageTextView.text = message
        errorMessageTextView.visibility = View.VISIBLE
    }
}
