package com.example.androsubmis2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androsubmis2.DetailActivity
import com.example.androsubmis2.R
import com.example.androsubmis2.adapters.EventAdapter
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.view.ViewEventModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton

class EventFragment : Fragment() {

    private lateinit var viewModel: ViewEventModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var searchAdapter: EventAdapter
    private lateinit var searchInput: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var errorMessageTextView: TextView
    private var isActiveEvents: Boolean = true // Determine if searching active events

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
        viewModel = ViewModelProvider(this)[ViewEventModel::class.java]
        observeEventLiveData()
        setupSearch()
        if (isActiveEvents) {
            viewModel.fetchActiveEvents()
        } else {
            viewModel.fetchCompletedEvents()
        }

        return view
    }
    private fun observeEventLiveData() {
        if (isActiveEvents) {
            // Observe active events
            viewModel.activeEventsLiveData.observe(viewLifecycleOwner) { events ->
                handleEventResults(events, "No active events found.")
            }
        } else {
            viewModel.completedEventsLiveData.observe(viewLifecycleOwner) { events ->
                handleEventResults(events, "No completed events found.")
            }
        }
        viewModel.searchResultsLiveData.observe(viewLifecycleOwner) { searchResults ->
            if (!searchResults.isNullOrEmpty()) {
                errorMessageTextView.visibility = View.GONE
                searchAdapter = EventAdapter(searchResults) { event -> onEventClicked(event) }
                recyclerView.adapter = searchAdapter
            } else {
                showErrorMessage("Not found")
            }
        }
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleEventResults(events: List<EventModel>?, emptyMessage: String) {
        if (!events.isNullOrEmpty()) {
            errorMessageTextView.visibility = View.GONE
            eventAdapter = EventAdapter(events) { event -> onEventClicked(event) }
            recyclerView.adapter = eventAdapter
        } else {
            showErrorMessage(emptyMessage)
        }
    }

    private fun setupSearch() {
        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchEvents(query, isActiveEvents)
            } else {
                Toast.makeText(context, "Please enter a search query", Toast.LENGTH_SHORT).show()
            }
        }

        searchInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchEvents(query, isActiveEvents)
                    return@OnEditorActionListener true
                } else {
                    Toast.makeText(context, "Please enter a search query", Toast.LENGTH_SHORT).show()
                }
            }
            false
        })
    }

    private fun onEventClicked(event: EventModel) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra("event_id", event.id)
        }
        startActivity(intent)
    }

    private fun showErrorMessage(message: String) {
        recyclerView.visibility = View.GONE
        errorMessageTextView.text = message
        errorMessageTextView.visibility = View.VISIBLE
    }
}
