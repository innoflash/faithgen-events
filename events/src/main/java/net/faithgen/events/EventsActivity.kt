package net.faithgen.events

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.faithgen.sdk.FaithGenActivity

class EventsActivity : FaithGenActivity() {
    override fun getPageTitle(): String {
        return Constants.EVENTS
    }

    override fun getPageIcon(): Int {
        return R.drawable.ic_calendar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
    }
}
