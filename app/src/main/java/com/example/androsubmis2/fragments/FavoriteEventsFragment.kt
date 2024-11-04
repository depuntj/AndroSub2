package com.example.androsubmis2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.androsubmis2.view.ViewFavoriteModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteEventsFragment : Fragment() {


    private val viewFavoriteModel: ViewFavoriteModel by viewModel()

    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var emptyStateTextView: TextView
    private lateinit var eventAdapter: EventAdapter
    private var favoriteEvents: MutableList<EventModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fav_list, container, false)

        favoriteRecyclerView = view.findViewById(R.id.favoriteRecyclerView)
        emptyStateTextView = view.findViewById(R.id.emptyStateText)

        favoriteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter(emptyList(), { event -> onEventClick(event) }, { event -> onFavoriteClick(event) })
        favoriteRecyclerView.adapter = eventAdapter

        observeFavoriteEvents()
        return view
    }

    private fun observeFavoriteEvents() {
        viewFavoriteModel.getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            if (favorites.isNullOrEmpty()) {
                showEmptyState(true)
            } else {
                showEmptyState(false)
                favoriteEvents = favorites.map { event ->
                    EventModel(
                        id = event.id.toInt(),
                        name = event.name,
                        beginTime = "",
                        ownerName = "",
                        quota = 0,
                        imageLogo = event.mediaCover,
                        isFavorite = true
                    )
                }.toMutableList()
                eventAdapter.updateEvents(favoriteEvents)
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        if (show) {
            favoriteRecyclerView.visibility = View.GONE
            emptyStateTextView.visibility = View.VISIBLE
        } else {
            favoriteRecyclerView.visibility = View.VISIBLE
            emptyStateTextView.visibility = View.GONE
        }
    }

    private fun onEventClick(event: EventModel) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra("event_id", event.id)
        }
        startActivity(intent)
    }

    private fun onFavoriteClick(event: EventModel) {
        val favoriteEntity = FavoriteEventEntity(
            id = event.id.toString(),
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

        val position = favoriteEvents.indexOfFirst { it.id == event.id }
        if (position != -1) {
            favoriteEvents[position].isFavorite = event.isFavorite
            eventAdapter.notifyItemChanged(position)
        }
    }
}
