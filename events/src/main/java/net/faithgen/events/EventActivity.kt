package net.faithgen.events

import android.os.Bundle
import net.faithgen.sdk.FaithGenActivity

class EventActivity :FaithGenActivity() {
    override fun getPageTitle(): String {
        return Constants.EVENT_DETAILS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_event_details)
    }
}