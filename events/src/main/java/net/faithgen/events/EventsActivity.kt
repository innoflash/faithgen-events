package net.faithgen.events

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.events.calendar.views.EventsCalendar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_events.*
import net.faithgen.events.adapters.EventsAdapter
import net.faithgen.events.models.APIDate
import net.faithgen.events.models.Event
import net.faithgen.events.models.EventsData
import net.faithgen.sdk.FaithGenActivity
import net.faithgen.sdk.http.API
import net.faithgen.sdk.http.ErrorResponse
import net.faithgen.sdk.http.types.ServerResponse
import net.faithgen.sdk.interfaces.DialogListener
import net.faithgen.sdk.singletons.GSONSingleton
import net.faithgen.sdk.utils.Dialogs
import net.faithgen.sdk.utils.Utils
import net.innoflash.iosview.recyclerview.RecyclerTouchListener
import net.innoflash.iosview.recyclerview.RecyclerViewClickListener
import java.lang.Exception
import java.util.*

class EventsActivity : FaithGenActivity(), EventsCalendar.Callback, RecyclerViewClickListener,
    PermissionListener {
    private var params = hashMapOf<String, String>()
    private var eventsList: List<Event>? = null
    private var dateEvents: List<Event>? = null
    private var eventsData: EventsData? = null
    private var queriedDate: String? = null

    override fun getPageTitle(): String {
        return Constants.EVENTS
    }

    override fun getPageIcon(): Int {
        return R.drawable.ic_calendar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        // toolbar.visibility = View.GONE

        eventsView.layoutManager = LinearLayoutManager(this)
        eventsView.addOnItemTouchListener(RecyclerTouchListener(this@EventsActivity, eventsView, this))

        selected.text = getDateString(eventsCalendar.getCurrentSelectedDate()?.timeInMillis)

        val today = Calendar.getInstance()
        val start = Calendar.getInstance()
        start.add(Calendar.YEAR, -6)
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 6)

        eventsCalendar.setSelectionMode(eventsCalendar.SINGLE_SELECTION)
            .setToday(today)
            .setMonthRange(start, end)
            .setWeekStartDay(Calendar.SUNDAY, false)
            .setIsBoldTextOnSelectionEnabled(true)
            .setCallback(this)
            .build()
    }

    override fun onStart() {
        super.onStart()
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_CALENDAR)
            .withListener(this)
            .check()
        if(eventsList === null){
            clearEvents()
            loadEvents(Calendar.getInstance())
        }
    }

    private fun loadEvents(currentDate: Calendar?) {
        val date = "${currentDate!!.get(Calendar.YEAR)}-${getMonth(currentDate)}-01"
        queriedDate = date
        params.put("date", date)
        API.get(this, Constants.ALL_EVENTS, params, false, object : ServerResponse() {
            override fun onServerResponse(serverResponse: String?) {
                eventsData = GSONSingleton.getInstance().gson.fromJson(
                    serverResponse,
                    EventsData::class.java
                )
                eventsList = eventsData!!.events
                showEventsOnCalendar()
            }

            override fun onError(errorResponse: ErrorResponse?) {
                Dialogs.showOkDialog(this@EventsActivity, errorResponse!!.message, false)
            }
        })
    }

    private fun showEventsOnCalendar() {
        val c = Calendar.getInstance()
        //eventsCalendar.clearEvents()
/*        eventsList!!.forEach {
            val c = Calendar.getInstance()
            //   c[Calendar.YEAR] = getAPIDate().year
           // c[Calendar.MONTH] = getAPIDate().month
            c[Calendar.DAY_OF_MONTH] = 15
            eventsCalendar.addEvent(c)
        }*/
    }

    private fun getAPIDate(): APIDate {
        var apiDate: APIDate = APIDate()
        var splitDate = queriedDate!!.split("-").toTypedArray()
        apiDate.year = Integer.parseInt(splitDate.get(0))
        apiDate.month = Integer.parseInt(splitDate.get(1))
        apiDate.date = Integer.parseInt(splitDate.get(2))
        return apiDate
    }

    private fun getDateString(time: Long?): String {
        if (time != null) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = time
            val month = when (cal[Calendar.MONTH]) {
                Calendar.JANUARY -> "January"
                Calendar.FEBRUARY -> "February"
                Calendar.MARCH -> "March"
                Calendar.APRIL -> "April"
                Calendar.MAY -> "May"
                Calendar.JUNE -> "June"
                Calendar.JULY -> "July"
                Calendar.AUGUST -> "August"
                Calendar.SEPTEMBER -> "September"
                Calendar.OCTOBER -> "October"
                Calendar.NOVEMBER -> "November"
                Calendar.DECEMBER -> "December"
                else -> ""
            }
            return "$month ${cal[Calendar.DAY_OF_MONTH]}, ${cal[Calendar.YEAR]}"
        } else return ""
    }

    override fun onDayLongPressed(selectedDate: Calendar?) {

    }

    override fun onDaySelected(selectedDate: Calendar?) {
        selected.text = getDateString(selectedDate?.timeInMillis)
        try {
            var dailyEvents = getDailyEvents(selectedDate)
            val adapter = EventsAdapter(
                this@EventsActivity,
                dailyEvents
            )
            eventsView.adapter = adapter
        }catch (ex : Exception){}
    }

    override fun onMonthChanged(monthStartDate: Calendar?) {
        clearEvents()
        loadEvents(monthStartDate)
    }

    private fun getDailyEvents(selectedDate: Calendar?) : List<Event>{
        dateEvents = eventsList!!.filter {
            val startWith = "${selectedDate!!.get(Calendar.YEAR)}-${getMonth(selectedDate)}-${getDay(selectedDate)}"
            it.start.exact.startsWith(startWith)
        }
        return dateEvents!!
    }

    private fun getMonth(selectedDate: Calendar?) : String {
        var currentMonth = selectedDate!!.get(Calendar.MONTH) + 1
        if(currentMonth > 12) currentMonth = 1

        if(currentMonth.toString().length < 2) return "0${currentMonth}"
        return currentMonth.toString()
    }

    private fun getDay(selectedDate: Calendar?) : String {
        if(selectedDate!!.get(Calendar.DAY_OF_MONTH).toString().length < 2)
            return "0${selectedDate.get(Calendar.DAY_OF_MONTH)}"
        return selectedDate.get(Calendar.DAY_OF_MONTH).toString()
    }

    override fun onClick(view: View?, position: Int) {
        val intent : Intent = Intent(this, EventActivity::class.java)
        intent.putExtra(Constants.EVENT_ID, dateEvents!!.get(position).id)
        startActivity(intent)
    }

    override fun onLongClick(view: View?, position: Int) {
        //leave as is
    }

    private fun clearEvents(){
        eventsView.adapter = EventsAdapter(
            this@EventsActivity,
            listOf()
        )
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        Toast.makeText(this, "Good to go", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {

    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Dialogs.confirmDialog(this, Constants.CALENDAR_PERMISSION, Constants.CALENDAR_PERMISSION_DENIED, object : DialogListener() {
            override fun onYes() {
                Utils.openSettings(this@EventsActivity)
            }
        })
    }
}
