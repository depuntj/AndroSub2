package com.example.androsubmis2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class EventFragment : Fragment() {

    private val viewModel: ViewEventModel by viewModel()
    private val viewFavoriteModel: ViewFavoriteModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private var eventsList: MutableList<EventModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        setupObservers()
        viewModel.fetchActiveEvents()

        return view
    }

    private fun setupObservers() {
        viewModel.activeEventsLiveData.observe(viewLifecycleOwner) { events ->
            if (!events.isNullOrEmpty()) {
                eventsList.clear()
                eventsList.addAll(events)
                setupAdapter(eventsList)
            } else {
                showErrorMessage("No active events found.")
            }
        }
    }

    private fun setupAdapter(events: List<EventModel>) {
        eventAdapter = EventAdapter(events, { event ->
            onEventClicked(event)
        }, { event ->
            onFavoriteClicked(event)
        })
        recyclerView.adapter = eventAdapter
    }

    private fun onEventClicked(event: EventModel) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra("event_id", event.id)
        }
        startActivity(intent)
    }

    private fun onFavoriteClicked(event: EventModel) {
        val favoriteEntity = FavoriteEventEntity(
            id = event.id?.toString() ?: "0",
            name = event.name ?: "Unknown Event",
            mediaCover = event.imageLogo
        )

        if (!event.isFavorite) {
            viewFavoriteModel.addFavorite(favoriteEntity)
            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
        } else {
            viewFavoriteModel.removeFavorite(favoriteEntity)
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
        }

        event.isFavorite = !event.isFavorite
        val eventIndex = eventsList.indexOf(event)
        if (eventIndex >= 0) {
            eventAdapter.notifyItemChanged(eventIndex)
        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
