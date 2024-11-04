package com.example.androsubmis2.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.androsubmis2.models.EventModel

class EventDiffCallback(
    private val oldList: List<EventModel>,
    private val newList: List<EventModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}