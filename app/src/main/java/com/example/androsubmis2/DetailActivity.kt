package com.example.androsubmis2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.models.FavoriteEventEntity
import com.example.androsubmis2.view.ViewEventModel
import com.example.androsubmis2.view.ViewFavoriteModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var eventImageView: ImageView
    private lateinit var eventNameTextView: TextView
    private lateinit var eventOwnerTextView: TextView
    private lateinit var eventTimeTextView: TextView
    private lateinit var eventQuotaTextView: TextView
    private lateinit var eventDescriptionTextView: TextView
    private lateinit var eventLinkButton: Button
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var fabFavorite: FloatingActionButton

    private var isFavorite: Boolean = false
    private var currentEvent: EventModel? = null

    private val viewFavoriteModel: ViewFavoriteModel by viewModel()
    private val viewEventModel: ViewEventModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize views
        eventImageView = findViewById(R.id.detail_event_image)
        eventNameTextView = findViewById(R.id.detail_event_name)
        eventOwnerTextView = findViewById(R.id.detail_event_owner)
        eventTimeTextView = findViewById(R.id.detail_event_time)
        eventQuotaTextView = findViewById(R.id.detail_event_quota)
        eventDescriptionTextView = findViewById(R.id.detail_event_description)
        eventLinkButton = findViewById(R.id.button_open_link)
        loadingIndicator = findViewById(R.id.loading_indicator)
        fabFavorite = findViewById(R.id.fab_favorite)

        val eventId = intent.getIntExtra("event_id", 0).toString()
        fetchEventDetail(eventId)

        // Apply theme-aware colors to button and FAB icon
        applyThemeColors()

        loadingIndicator.visibility = ProgressBar.VISIBLE
        viewEventModel.eventDetailLiveData.observe(this) { event ->
            event?.let {
                currentEvent = it
                displayEventDetails(it)
                checkIfEventIsFavorite(it.id.toString())
            }
        }
        fabFavorite.setOnClickListener {
            toggleFavorite()
        }
        eventLinkButton.setOnClickListener {
            val eventLink = viewEventModel.eventDetailLiveData.value?.link
            eventLink?.let { link ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(browserIntent)
            } ?: Toast.makeText(this, getString(R.string.event_link_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchEventDetail(eventId: String) {
        viewEventModel.fetchEventDetail(eventId.toInt())
    }

    private fun displayEventDetails(event: EventModel) {
        loadingIndicator.visibility = ProgressBar.GONE
        Glide.with(this)
            .load(event.imageLogo)
            .into(eventImageView)

        eventNameTextView.text = event.name ?: getString(R.string.unknown_event)
        eventOwnerTextView.text = event.ownerName ?: getString(R.string.unknown_owner)
        eventTimeTextView.text = event.beginTime ?: getString(R.string.unknown_time)
        eventQuotaTextView.text = getString(
            R.string.event_quota_placeholder,
            (event.quota ?: 0) - (event.registrants ?: 0)
        )

        eventDescriptionTextView.text = Html.fromHtml(event.description ?: "", Html.FROM_HTML_MODE_COMPACT)
        eventDescriptionTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun checkIfEventIsFavorite(eventId: String) {
        viewFavoriteModel.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            isFavorite = favoriteEvent != null
            updateFavoriteIcon()
        }
    }

    private fun toggleFavorite() {
        currentEvent?.let { event ->
            val favoriteEntity = FavoriteEventEntity(
                id = event.id.toString(),
                name = event.name ?: getString(R.string.unknown_event),
                mediaCover = event.imageLogo
            )

            if (isFavorite) {
                viewFavoriteModel.removeFavorite(favoriteEntity)
                showToast(getString(R.string.removed_from_favorites))
            } else {
                viewFavoriteModel.addFavorite(favoriteEntity)
                showToast(getString(R.string.added_to_favorites))
            }

            isFavorite = !isFavorite
            updateFavoriteIcon()

            val resultIntent = Intent().apply {
                putExtra("event_id", event.id)
                putExtra("is_favorite", isFavorite)
            }
            setResult(RESULT_OK, resultIntent)
        }
    }

    private fun updateFavoriteIcon() {
        val iconRes = if (isFavorite) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }
        fabFavorite.setImageResource(iconRes)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun applyThemeColors() {
        // Set colors for the button and FAB from the current theme
        eventLinkButton.setBackgroundColor(
            ContextCompat.getColor(this, R.color.purple_700)
        )
        fabFavorite.backgroundTintList = ContextCompat.getColorStateList(this, R.color.favorite_icon_color)
    }
}
