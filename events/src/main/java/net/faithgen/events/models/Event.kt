package net.faithgen.events.models

import net.faithgen.sdk.models.Avatar
import net.faithgen.sdk.models.Date

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val video_url: String,
    val date: String,
    val published: Boolean,
    val is_past: Boolean,
    val start: Date,
    val end: Date,
    val location: Location,
    val avatar: Avatar,
    val guests: List<Guest>
)