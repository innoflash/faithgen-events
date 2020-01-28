package net.faithgen.events

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.faithgen.events.models.Event
import net.faithgen.events.view_holders.EventViewHolder

class EventsAdapter(val context : Context, val events : List<Event>): RecyclerView.Adapter<EventViewHolder>() {
    private var layoutInflater: LayoutInflater?

    init {
        layoutInflater = LayoutInflater.from(context)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(layoutInflater!!.inflate(R.layout.list_item_event, parent, false))
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.getEventsView()!!.itemHeading = "we good"
    }
}