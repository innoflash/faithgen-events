package net.faithgen.events.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.innoflash.iosview.lists.ListItemView3

class EventViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var eventView: ListItemView3?

    fun getEventsView(): ListItemView3? {
        return eventView
    }
    init {
        eventView = itemView as ListItemView3?
    }
}