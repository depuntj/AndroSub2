package com.example.androsubmis2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androsubmis2.databinding.ItemEventBinding
import com.example.androsubmis2.models.EventModel

class EventAdapter(
    private var events: List<EventModel>,
    private val onClick: (EventModel) -> Unit
) : RecyclerView.Adapter<EventHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val event = events[position]
        holder.bind(event, onClick)
    }

    override fun getItemCount(): Int = events.size
}
