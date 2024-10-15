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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.view.ViewEventModel

class DetailActivity : AppCompatActivity() {

    private val viewEventModel: ViewEventModel by viewModels()
    private lateinit var eventImageView: ImageView
    private lateinit var eventNameTextView: TextView
    private lateinit var eventOwnerTextView: TextView
    private lateinit var eventTimeTextView: TextView
    private lateinit var eventQuotaTextView: TextView
    private lateinit var eventDescriptionTextView: TextView
    private lateinit var eventLinkButton: Button
    private lateinit var loadingindicator : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        eventImageView = findViewById(R.id.detail_event_image)
        eventNameTextView = findViewById(R.id.detail_event_name)
        eventOwnerTextView = findViewById(R.id.detail_event_owner)
        eventTimeTextView = findViewById(R.id.detail_event_time)
        eventQuotaTextView = findViewById(R.id.detail_event_quota)
        eventDescriptionTextView = findViewById(R.id.detail_event_description)
        eventLinkButton = findViewById(R.id.button_open_link)
        loadingindicator = findViewById(R.id.loading_indicator)

        val eventId = intent.getIntExtra("event_id", 0)
        fetchEventDetail(eventId)
        loadingindicator.visibility = ProgressBar.VISIBLE

        viewEventModel.eventDetailLiveData.observe(this) { event ->
            event?.let {
                displayEventDetails(it)
            } ?: run {
                Toast.makeText(this, getString(R.string.event_not_found), Toast.LENGTH_SHORT).show()
            }
        }
        viewEventModel.errorMessageLiveData.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        eventLinkButton.setOnClickListener {
            val eventLink = viewEventModel.eventDetailLiveData.value?.link
            eventLink?.let { link ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(browserIntent)
            } ?: Toast.makeText(this, getString(R.string.event_link_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchEventDetail(eventId: Int) {
        viewEventModel.fetchEventDetail(eventId)
    }

    private fun displayEventDetails(event: EventModel) {
        loadingindicator.visibility = ProgressBar.GONE
        Glide.with(this)
            .load(event.imageLogo)
            .into(eventImageView)

        eventNameTextView.text = event.name
        eventOwnerTextView.text = event.ownerName
        eventTimeTextView.text = event.beginTime
        eventQuotaTextView.text = getString(
            R.string.event_quota_placeholder,
            (event.quota ?: 0) - (event.registrants ?: 0)
        )

        eventDescriptionTextView.text = Html.fromHtml(event.description ?: "", Html.FROM_HTML_MODE_COMPACT)
        eventDescriptionTextView.movementMethod = LinkMovementMethod.getInstance()
    }
}
