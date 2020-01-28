package net.faithgen.events.models

import com.google.gson.annotations.SerializedName
import net.faithgen.sdk.models.Date

class Event {
    var id : String? = null
    var name : String? = null
    var description : String? = null
    var date : String? = null
    var published: Boolean? = null
    @SerializedName("is_past")
    var isPast: Boolean = false
    var start: Date? = null
    var end: Date? = null
    var location: Location? = null
}