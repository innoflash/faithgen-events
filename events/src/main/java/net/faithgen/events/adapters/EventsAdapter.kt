package net.faithgen.events.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.faithgen.events.R
import net.faithgen.events.models.Event
import net.faithgen.events.view_holders.LI3Holder
import net.faithgen.sdk.SDK
import net.innoflash.iosview.lists.ListItemView3

final class EventsAdapter(private val context: Context, private val events: List<Event>) :
    RecyclerView.Adapter<LI3Holder>() {

    private val layoutInflater: LayoutInflater by lazy {
       LayoutInflater.from(this.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LI3Holder {
        return LI3Holder(
            layoutInflater.inflate(
                R.layout.list_item_obj,
                parent,
                false
            ) as ListItemView3
        )
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: LI3Holder, position: Int) {
        val event = events.get(position)

        holder.itemView3.itemHeading = SDK.getMinistry().name
        holder.itemView3.itemContent = event.name
        holder.itemView3.itemFooter = event.date
        if (event.is_past) {
            holder.itemView3.isHasType = true
            holder.itemView3.badge = "past"
            holder.itemView3.itemType = ListItemView3.ItemType.RED
        }

        if(event.avatar != null) {
            holder.itemView3.isCircularImage = true
            Picasso.get()
                .load(event.avatar._50)
                .placeholder(R.drawable.main_calendar)
                .error(R.drawable.main_calendar)
                .into(holder.itemView3.circleImageView)
        }
    }
}