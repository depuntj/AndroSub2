package com.example.androsubmis2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActiveEventsFragment : Fragment() {

    private val viewModel: ViewEventModel by viewModel()
    private val viewFavoriteModel: ViewFavoriteModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var searchInput: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var errorMessageTextView: TextView
    private lateinit var loadingIndicator: ProgressBar
    private var activeEventsList: MutableList<EventModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        searchInput = view.findViewById(R.id.search_input)
        searchButton = view.findViewById(R.id.button_search)
        errorMessageTextView = view.findViewById(R.id.empty_state_text)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        setupObservers()
        setupSearch()

        viewModel.fetchActiveEvents()

        return view
    }

    private fun setupObservers() {
        loadingIndicator.visibility = View.VISIBLE

        viewModel.activeEventsLiveData.observe(viewLifecycleOwner) { events ->
            loadingIndicator.visibility = View.GONE
            if (!events.isNullOrEmpty()) {
                activeEventsList.clear()
                activeEventsList.addAll(events)
                showData(activeEventsList)
            } else {
                showErrorMessage("No active events found.")
            }
        }

        viewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showErrorMessage(it)
            }
        }
    }

    private fun setupSearch() {
        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                filterActiveEvents(query)
            } else {
                Toast.makeText(context, "Please enter a search query", Toast.LENGTH_SHORT).show()
            }
        }

        searchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    filterActiveEvents(query)
                    true
                } else {
                    Toast.makeText(context, "Please enter a search query", Toast.LENGTH_SHORT).show()
                    false
                }
            } else {
                false
            }
        }
    }

    private fun filterActiveEvents(query: String) {
        val filteredList = activeEventsList.filter { event ->
            event.name?.contains(query, ignoreCase = true) == true || event.beginTime?.contains(query, ignoreCase = true) == true
        }

        if (filteredList.isNotEmpty()) {
            showData(filteredList)
        } else {
            showErrorMessage("No search results found.")
        }
    }

    private fun showData(events: List<EventModel>) {
        errorMessageTextView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        eventAdapter = EventAdapter(events, { event: EventModel ->
            onEventClicked(event)
        }, { event: EventModel ->
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
        val eventIndex = activeEventsList.indexOf(event)
        if (eventIndex >= 0) {
            eventAdapter.notifyItemChanged(eventIndex)
        }
    }

    private fun showErrorMessage(message: String) {
        recyclerView.visibility = View.GONE
        errorMessageTextView.text = message
        errorMessageTextView.visibility = View.VISIBLE
    }
}
