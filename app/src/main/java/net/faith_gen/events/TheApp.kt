package net.faith_gen.events

import android.app.Application
import net.faithgen.sdk.SDK
import nouri.`in`.goodprefslib.GoodPrefs
import java.io.IOException

class TheApp: Application() {

    override fun onCreate() {
        super.onCreate()
        GoodPrefs.init(this)
        try {
            SDK.initializeSDK(this, this.assets.open("config.json"), resources.getString(R.color.colorPrimary), null)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}