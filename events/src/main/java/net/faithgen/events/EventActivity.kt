package net.faithgen.events

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event_details.*
import net.faithgen.events.adapters.GuestsAdapter
import net.faithgen.events.models.Event
import net.faithgen.sdk.FaithGenActivity
import net.faithgen.sdk.SDK
import net.faithgen.sdk.comments.CommentsSettings
import net.faithgen.sdk.enums.CommentsDisplay
import net.faithgen.sdk.http.API
import net.faithgen.sdk.http.ErrorResponse
import net.faithgen.sdk.http.types.ServerResponse
import net.faithgen.sdk.menu.MenuFactory
import net.faithgen.sdk.menu.MenuItem
import net.faithgen.sdk.singletons.GSONSingleton
import net.faithgen.sdk.utils.Dialogs
import net.faithgen.sdk.utils.Utils

class EventActivity : FaithGenActivity() {

    var event: Event? = null
    val menuItems = mutableListOf<MenuItem>()
    private val eventId: String by lazy {
        intent.getStringExtra(Constants.EVENT_ID)
    }

    override fun getPageTitle(): String {
        return Constants.EVENT_DETAILS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
        eventGuests.layoutManager = LinearLayoutManager(this)

        initMenu()
    }

    private fun initMenu() {
        menuItems.add(MenuItem(R.drawable.ic_comment_24, Constants.COMMENT))
      //  if (event?.video_url !== null)
            menuItems.add(MenuItem(R.drawable.ic_watch_video, Constants.WATCH_VIDEO))
        val menu = MenuFactory.initializeMenu(this, menuItems)

        menu.setOnMenuItemListener(Constants.MENU) { menuItem, position ->
            when (position) {
                0 -> SDK.openComments(
                    this@EventActivity,
                    CommentsSettings.Builder()
                        .setTitle(event!!.name)
                        .setItemId(eventId)
                        .setCategory("events/")
                        .setCommentsDisplay(CommentsDisplay.DIALOG)
                        .build()
                )
                1 -> Utils.openURL(this@EventActivity, event!!.video_url)
            }
        }

        setOnOptionsClicked { menu.show() }
    }

    override fun onStart() {
        super.onStart()
        if (event === null) getEvent()
    }

    private fun getEvent() {
        API.get(
            this@EventActivity,
            Constants.ALL_EVENTS + "/$eventId",
            null,
            false,
            object : ServerResponse() {
                override fun onServerResponse(serverResponse: String?) {
                    event =
                        GSONSingleton.getInstance().gson.fromJson(serverResponse, Event::class.java)
                    renderEvent(event)
                }

                override fun onError(errorResponse: ErrorResponse?) {
                    //super.onError(errorResponse)
                    Dialogs.showOkDialog(this@EventActivity, errorResponse?.message, true)
                }
            })
    }

    private fun displayGuests() {
        val adapter = GuestsAdapter(this, event!!.guests)
        eventGuests.adapter = adapter
    }

    private fun renderEvent(event: Event?) {
        toolbar.pageTitle = event?.name
        eventName.text = event?.name
        eventDescription!!.text = event?.description
        eventStart!!.content = "${event?.start?.formatted} : ${event?.start?.time}"
        eventEnd!!.content = "${event?.end?.formatted} : ${event?.end?.time}"
        eventLocation.itemHeading = event?.location?.address?.name
        eventLocation.itemContent = event?.location?.locality
        eventLocation.itemFooter = event?.location?.country

        /**
         * Displays the guests
         */
        when (event?.guests?.size) {
            0 -> hasGuests.visibility = View.GONE
            else -> displayGuests()
        }

        /**
         * Shows the event banner
         */
        when (event?.avatar) {
            null -> eventBanner.visibility = View.GONE
            else -> {
                eventBanner.visibility = View.VISIBLE
                Picasso.get()
                    .load(event.avatar.original)
                    .placeholder(R.drawable.main_calendar)
                    .error(R.drawable.main_calendar)
                    .into(eventBanner)
            }
        }

        /**
         * Shows event links
         */
        if (event?.url === null && event?.video_url === null) {
            linksWrapper.visibility = View.GONE
            hasUrls.visibility = View.GONE
        } else {
            /**
             * Shows the link if there
             */
            when (event?.url) {
                null -> eventLink.visibility = View.GONE
                else -> eventLink.setOnClickListener { view ->
                    Utils.openURL(
                        this@EventActivity,
                        event.url
                    )
                }
            }

            /**
             * Shows the video url if there
             */
            when (event?.video_url) {
                null -> eventVideoLink.visibility = View.GONE
                else -> eventVideoLink.setOnClickListener { view ->
                    Utils.openURL(
                        this@EventActivity,
                        event.video_url
                    )
                }
            }
        }
    }
}