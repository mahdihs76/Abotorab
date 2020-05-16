package ir.nilva.abotorab

import androidx.multidex.MultiDexApplication
import com.microblink.MicroblinkSDK
import es.dmoral.toasty.Toasty
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.helper.defaultCache
import ir.nilva.abotorab.helper.get
import ir.nilva.abotorab.helper.getAppTypeface

/**
 *
 * Created by mahdihs76 on 9/11/18.
 */
class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        ApplicationContext.initialize(this)
        val blinkId = defaultCache()["BLINK_ID"] ?: ""
        if (blinkId.isNotEmpty()) {
            MicroblinkSDK.setLicenseKey(blinkId, this)
        }
        AppDatabase.getInstance()

        Toasty.Config.getInstance()
            .setToastTypeface(getAppTypeface())
            .setTextSize(14)
            .apply()
    }
}