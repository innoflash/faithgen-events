package net.faithgen.events.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.faithgen.events.R
import net.faithgen.events.models.Guest
import net.faithgen.events.view_holders.LI3Holder
import net.innoflash.iosview.lists.ListItemView3

class GuestsAdapter(val context: Context, val guests: List<Guest>): RecyclerView.Adapter<LI3Holder>(){
    private val layoutInflater : LayoutInflater by lazy {
        LayoutInflater.from(context)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LI3Holder {
        val li3View = layoutInflater.inflate(R.layout.list_item_obj, parent, false) as ListItemView3
        return LI3Holder(li3View)
    }

    override fun getItemCount(): Int = guests.size

    override fun onBindViewHolder(holder: LI3Holder, position: Int) {
        val guest: Guest = guests.get(position)
        holder.itemView3.isCircularImage = true
        Picasso.get()
            .load(guest.avatar._50)
            .placeholder(R.drawable.user)
            .error(R.drawable.user)
            .into(holder.itemView3.circleImageView)

        holder.itemView3.itemHeading = guest.title
        holder.itemView3.itemContent = guest.name
        holder.itemView3.itemFooter = "guest"
    }

}