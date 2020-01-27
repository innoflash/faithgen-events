package net.faith_gen.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import net.faithgen.events.EventsActivity
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openEvents.setOnClickListener { view ->
            startActivity(Intent(this, EventsActivity::class.java))
        }
    }
}
