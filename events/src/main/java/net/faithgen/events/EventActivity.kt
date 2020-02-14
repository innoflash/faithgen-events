package net.faithgen.events

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event_details.*
import net.faithgen.events.adapters.GuestsAdapter
import net.faithgen.events.models.Date
import net.faithgen.events.models.Event
import net.faithgen.sdk.FaithGenActivity
import net.faithgen.sdk.SDK
import net.faithgen.sdk.comments.CommentsSettings
import net.faithgen.sdk.enums.CommentsDisplay
import net.faithgen.sdk.http.API
import net.faithgen.sdk.http.ErrorResponse
import net.faithgen.sdk.http.types.ServerResponse
import net.faithgen.sdk.interfaces.DialogListener
import net.faithgen.sdk.menu.MenuFactory
import net.faithgen.sdk.menu.MenuItem
import net.faithgen.sdk.singletons.GSONSingleton
import net.faithgen.sdk.utils.Dialogs
import net.faithgen.sdk.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class EventActivity : FaithGenActivity(), OnMapReadyCallback {

    private var event: Event? = null
    private val menuItems = mutableListOf<MenuItem>()
    private val simpleDateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm")
    }
    private val mapFragment: SupportMapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }
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
    }

    private fun initMenu() {
        menuItems.add(MenuItem(R.drawable.ic_comment_24, Constants.COMMENT))
        menuItems.add(MenuItem(R.drawable.ic_share, Constants.INVITE_OTHERS))
        menuItems.add(MenuItem(R.drawable.ic_navigate, Constants.NAVIGATE_VENUE))
        menuItems.add(MenuItem(R.drawable.ic_calendar_add, Constants.MARK_ON_CALENDAR))
        if (event?.video_url !== null)
            menuItems.add(MenuItem(R.drawable.ic_watch_video, Constants.WATCH_VIDEO))

        val menu = MenuFactory.initializeMenu(this, menuItems)

        menu.setOnMenuItemListener(Constants.MENU) { _, position ->
            when (position) {
                0 -> openComments()
                1 -> Utils.shareText(this@EventActivity, getInvitation())
                2 -> Utils.openURL(this@EventActivity, event?.location?.place_url)
                3 -> addToCalendar()
                4 -> Utils.openURL(this@EventActivity, event!!.video_url)
            }
        }
        setOnOptionsClicked { menu.show() }
    }

    override fun onStart() {
        super.onStart()
        if (event === null) getEvent()
    }

    private fun addToCalendar() {
        if (checkCallingOrSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            val calendarParams = ContentValues()
            val timeZone = TimeZone.getDefault()
            calendarParams.put(CalendarContract.Events.TITLE, event?.name)
            calendarParams.put(CalendarContract.Events.CALENDAR_ID, 1)
            calendarParams.put(CalendarContract.Events.DESCRIPTION, event?.description)
            calendarParams.put(
                CalendarContract.Events.DTSTART,
                getDateInMilliSeconds(event!!.start)
            )
            calendarParams.put(CalendarContract.Events.DTEND, getDateInMilliSeconds(event!!.end))
            calendarParams.put(
                CalendarContract.Events.EVENT_LOCATION,
                event?.location?.address?.formatted
            )
            calendarParams.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.id)

            val uri: Uri? =
                contentResolver.insert(CalendarContract.Events.CONTENT_URI, calendarParams)
            when (uri) {
                null -> Dialogs.showOkDialog(this, Constants.FAILED_TO_ADD_CALENDAR, false)
                else -> Dialogs.showOkDialog(this, Constants.SUCCEEDED_TO_ADD_CALENDAR, false)
            }
        } else Dialogs.confirmDialog(
            this,
            Constants.CALENDAR_PERMISSION,
            Constants.CALENDAR_DENIED_PERMISSION,
            object : DialogListener() {
                override fun onYes() {
                    Utils.openSettings(this@EventActivity)
                }
            })
    }

    private fun getDateInMilliSeconds(date: Date): Long {
        val sanitizedDate: String =
            date.exact.substring(0, date.exact.indexOf('T')) + " ${date.time}"
        val requiredDate: java.util.Date? = simpleDateFormat.parse(sanitizedDate)
        return requiredDate!!.time;
    }

    private fun openComments() {
        return SDK.openComments(
            this@EventActivity,
            CommentsSettings.Builder()
                .setTitle(event!!.name)
                .setItemId(eventId)
                .setCategory("events/")
                .setCommentsDisplay(CommentsDisplay.DIALOG)
                .build()
        )
    }

    private fun getInvitation(): String {
        return "Hey there, i am inviting you to an event named \"${event?.name}\" hosted by ${SDK.getMinistry().name}\n" +
                "Its taking place on ${event?.start?.formatted} starting at ${event?.start?.time}\n\n" +
                "For more information visit:\n" +
                "https://faithgen.com/events/$eventId"
    }

    private fun getEvent() {
        API.get(
            this@EventActivity,
            Constants.ALL_EVENTS + "/$eventId",
            null,
            false,
            object : ServerResponse() {
                override fun onServerResponse(serverResponse: String?) {
                    event = GSONSingleton.getInstance().gson
                        .fromJson(serverResponse, Event::class.java)
                    renderEvent(event)
                    initMenu()
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
        eventDescription.text = event?.description
        eventStart.content = "${event?.start?.formatted} : ${event?.start?.time}"
        eventEnd.content = "${event?.end?.formatted} : ${event?.end?.time}"
        eventLocation.itemHeading = event?.location?.address?.name
        eventLocation.itemContent = event?.location?.locality
        eventLocation.itemFooter = event?.location?.country

        mapFragment.getMapAsync(this)

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
                    Utils.openURL(this@EventActivity, event.url)
                }
            }

            /**
             * Shows the video url if there
             */
            when (event?.video_url) {
                null -> eventVideoLink.visibility = View.GONE
                else -> eventVideoLink.setOnClickListener { view ->
                    Utils.openURL(this@EventActivity, event.video_url)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if(event !== null){
            val position = LatLng(
                event!!.location.coordinates.lat,
                event!!.location.coordinates.lng
            )
            googleMap.setContentDescription(event?.name)
            googleMap.addMarker(
                MarkerOptions().position(position).draggable(false).snippet(event!!.name)
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
            googleMap.isMyLocationEnabled = false
            googleMap.uiSettings.isScrollGesturesEnabled = false
        }
    }
}