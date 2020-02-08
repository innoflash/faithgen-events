package net.faithgen.events.models

data class Date(
    val time: String,
    var approx: String?,
    val formatted: String,
    val exact: String
)