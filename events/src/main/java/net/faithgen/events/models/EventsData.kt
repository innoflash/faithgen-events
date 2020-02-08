package net.faithgen.events.models

import com.google.gson.annotations.SerializedName

data class EventsData(
    @SerializedName("data")
    val events : List<Event>
)
