package com.example.androsubmis2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androsubmis2.DetailActivity
import com.example.androsubmis2.R
import com.example.androsubmis2.adapters.EventAdapter
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.view.ViewEventModel

class HomeFragment : Fragment() {

    private lateinit var viewModel: ViewEventModel
    private lateinit var recyclerActiveEvents: RecyclerView
    private lateinit var recyclerCompletedEvents: RecyclerView
    private lateinit var eventAdapterActive: EventAdapter
    private lateinit var eventAdapterCompleted: EventAdapter
    private lateinit var errorMessageTextView: TextView
    private lateinit var loadingIndicatorActive: ProgressBar
    private lateinit var loadingIndicatorCompleted: ProgressBar

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

        viewModel = ViewModelProvider(this)[ViewEventModel::class.java]
        loadingIndicatorActive.visibility = View.VISIBLE
        viewModel.activeEventsLiveData.observe(viewLifecycleOwner) { events ->
            loadingIndicatorActive.visibility = View.GONE
            if (!events.isNullOrEmpty()) {
                errorMessageTextView.visibility = View.GONE
                eventAdapterActive = EventAdapter(events.take(5)) { event -> onEventClicked(event) }
                recyclerActiveEvents.adapter = eventAdapterActive
            } else {
                showErrorMessage("No active events available.")
            }
        }
        loadingIndicatorCompleted.visibility = View.VISIBLE
        viewModel.completedEventsLiveData.observe(viewLifecycleOwner) { events ->
            loadingIndicatorCompleted.visibility = View.GONE
            if (!events.isNullOrEmpty()) {
                errorMessageTextView.visibility = View.GONE
                eventAdapterCompleted = EventAdapter(events.take(5)) { event -> onEventClicked(event) }
                recyclerCompletedEvents.adapter = eventAdapterCompleted
            } else {
                showErrorMessage("No completed events available.")
            }
        }
        viewModel.fetchActiveEvents()
        viewModel.fetchCompletedEvents()
        return view
    }

    private fun onEventClicked(event: EventModel) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra("event_id", event.id)
        }
        startActivity(intent)
    }

    private fun showErrorMessage(message: String) {
        errorMessageTextView.text = message
        errorMessageTextView.visibility = View.VISIBLE
    }
}
