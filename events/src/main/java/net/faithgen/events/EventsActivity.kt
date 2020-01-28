package net.faithgen.events

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import net.faithgen.sdk.FaithGenActivity

class EventsActivity : FaithGenActivity() {
    val daysOfWeek = daysOfWeekFromLocale()
    override fun getPageTitle(): String {
        return Constants.EVENTS
    }

    override fun getPageIcon(): Int {
        return R.drawable.ic_calendar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        toolbar.visibility = View.GONE
    }
}
