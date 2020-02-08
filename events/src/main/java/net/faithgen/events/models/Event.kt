package net.faithgen.events.models

import com.google.gson.annotations.SerializedName

data class Event(
    val id : String,
    val name : String,
    val description : String,
    val date : String,
    val published: Boolean,
    @SerializedName("is_past")
    val isPast: Boolean,
    val start: Date,
    val end: Date,
    val location: Location
)