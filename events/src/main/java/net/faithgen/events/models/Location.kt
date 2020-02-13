package net.faithgen.events.models


data class Location(
    val country: String,
    val locality: String,
    val place_id: String,
    val place_url: String,
    val postal_code: String,
    val address: Address,
    val coordinates: Coordinates
) {
    public data class Address(
        val name: String,
        val formatted: String
    )
    public data class Coordinates(
        val lat : Double,
        val lng : Double
    )
}