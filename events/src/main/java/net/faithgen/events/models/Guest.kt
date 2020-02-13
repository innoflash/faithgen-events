package net.faithgen.events.models

import net.faithgen.sdk.models.Avatar

data class Guest(
    val id: String,
    val title: String,
    val name: String,
    val avatar: Avatar
)