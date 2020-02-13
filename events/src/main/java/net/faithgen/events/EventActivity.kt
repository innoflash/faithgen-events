package net.faithgen.events

import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event_details.*
import net.faithgen.events.models.Event
import net.faithgen.sdk.FaithGenActivity
import net.faithgen.sdk.http.API
import net.faithgen.sdk.http.ErrorResponse
import net.faithgen.sdk.http.types.ServerResponse
import net.faithgen.sdk.singletons.GSONSingleton
import net.faithgen.sdk.utils.Dialogs

class EventActivity :FaithGenActivity() {

    var event : Event? = null
    private val eventId: String by lazy {
        intent.getStringExtra(Constants.EVENT_ID)
    }

    override fun getPageTitle(): String {
        return Constants.EVENT_DETAILS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
    }

    override fun onStart() {
        super.onStart()
        if(event === null) getEvent()
    }

    private fun getEvent(){
        API.get(this@EventActivity, Constants.ALL_EVENTS +"/$eventId", null, false, object : ServerResponse() {
            override fun onServerResponse(serverResponse: String?) {
                event  = GSONSingleton.getInstance().gson.fromJson(serverResponse, Event::class.java)
                renderEvent(event)
            }

            override fun onError(errorResponse: ErrorResponse?) {
                //super.onError(errorResponse)
                Dialogs.showOkDialog(this@EventActivity, errorResponse?.message, true)
            }
        })
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

        when(event?.avatar){
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
    }
}