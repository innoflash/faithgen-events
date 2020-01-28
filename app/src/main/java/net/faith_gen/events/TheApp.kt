package net.faith_gen.events

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import net.faithgen.sdk.SDK
import nouri.`in`.goodprefslib.GoodPrefs
import java.io.IOException

class TheApp: Application() {

    override fun onCreate() {
        super.onCreate()
        GoodPrefs.init(this)
        AndroidThreeTen.init(this)
        try {
            SDK.initializeSDK(this, this.assets.open("config.json"), resources.getString(R.color.colorPrimary), null)
            SDK.initializeApiBase("http://192.168.0.112:8001/api/")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}