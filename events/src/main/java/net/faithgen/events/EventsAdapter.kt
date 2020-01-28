package net.faithgen.events

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.faithgen.events.models.Event
import net.faithgen.events.view_holders.EventViewHolder
import net.faithgen.sdk.SDK
import net.innoflash.iosview.lists.ListItemView3

class EventsAdapter(private val context: Context, private val events: List<Event>) :
    RecyclerView.Adapter<EventViewHolder>() {
    private var layoutInflater: LayoutInflater?

    init {
        layoutInflater = LayoutInflater.from(this.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            layoutInflater!!.inflate(
                R.layout.list_item_event,
                parent,
                false
            ) as ListItemView3
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.eventView.itemHeading = SDK.getMinistry().name
        holder.eventView.itemContent = events.get(position).name
        holder.eventView.itemFooter = events.get(position).date
        if (events.get(position).isPast) {
            holder.eventView.isHasType = true
            holder.eventView.badge = "past"
            holder.eventView.itemType = ListItemView3.ItemType.RED
        }
    }
}