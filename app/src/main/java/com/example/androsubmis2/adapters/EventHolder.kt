package com.example.androsubmis2.adapters

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androsubmis2.R
import com.example.androsubmis2.databinding.ItemEventBinding
import com.example.androsubmis2.models.EventModel

class EventHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        event: EventModel,
        onClick: (EventModel) -> Unit,
        onFavoriteClick: (EventModel) -> Unit
    ) {
        binding.eventName.text = event.name ?: "Event Name"
        binding.eventTime.text = event.beginTime ?: "Event Time"
        binding.imageLoadingIndicator.visibility = android.view.View.VISIBLE

        Glide.with(binding.root.context)
            .load(event.imageLogo)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.imageLoadingIndicator.visibility = android.view.View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.imageLoadingIndicator.visibility = android.view.View.GONE
                    return false
                }
            })
            .into(binding.eventImage)

        binding.root.setOnClickListener { onClick(event) }

        updateFavoriteIcon(event.isFavorite)

        binding.favoriteButton.setOnClickListener {
            onFavoriteClick(event)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }
}
