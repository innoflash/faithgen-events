package net.faithgen.events.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.faithgen.events.R
import net.faithgen.events.models.Event
import net.innoflash.iosview.DialogFullScreen
import net.innoflash.iosview.DialogToolbar
import net.innoflash.iosview.lists.ListItemView2
import net.innoflash.iosview.lists.ListItemView3

final class EventDetails(private val event: Event) : DialogFullScreen() {

    private var dialogToolbar: DialogToolbar? = null
    private var eventName: TextView? = null
    private var eventDescription: TextView? = null
    private var eventStart: ListItemView2? = null
    private var eventEnd: ListItemView2? = null
    private var eventLocation: ListItemView3? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_event_details, container, false)
        //dialogToolbar = rootView.findViewById(R.id.dialogToolbar) as DialogToolbar
        eventName = rootView.findViewById(R.id.eventName) as TextView
        eventDescription = rootView.findViewById(R.id.eventDescription) as TextView
        eventStart = rootView.findViewById(R.id.eventStart) as ListItemView2
        eventEnd = rootView.findViewById(R.id.eventEnd) as ListItemView2
        eventLocation = rootView.findViewById(R.id.eventLocation) as ListItemView3

        dialogToolbar!!.dialogFragment = this
        return rootView
    }

    override fun onStart() {
        super.onStart()
       // dialogToolbar!!.title = event.name
        eventName!!.text = event.name
        eventDescription!!.text = event.description
        eventStart!!.content = "${event.start.formatted} : ${event.start.time}"
        eventEnd!!.content = "${event.end.formatted} : ${event.end.time}"
        //todo fill up the location
    }
}