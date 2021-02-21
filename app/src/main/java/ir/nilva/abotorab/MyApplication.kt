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
//            MicroblinkSDK.setLicenseKey(
//                "sRwAAAARaXIubmlsdmEuYWJvdG9yYWL8E5lL5HoRkcC3kG/p6hqSYHdue5GI/E3hkFV/JteCArXnC8patymryNX8CRObRrS7YO9o7vdrKbons6mUu4MAa0E1Wzdr2wZQbEz8SM+siMOk8WcLB2irqUONEO/+b4URAp6PiSGN5bl/mxYg3BMvVE0DyBTNNfbjjb6HoQUGQolNVBELrsdt4UoA0uoAFB1fDFy2/thZKDD+vjfAXjFGmzYphKr0nP6uOZUwBaLAa3Y/6hQVEaIDvQmdxK+meeT5e0MxZOpUxNMtQeYGqJ55w6wGtQYLCNVej0oNkvrTCN93kUxUrZ/8qk6F7DGJy7EtwMI7Q5g=",
//                this
//            )
        }
        AppDatabase.getInstance()

        Toasty.Config.getInstance()
            .setToastTypeface(getAppTypeface())
            .setTextSize(14)
            .apply()
    }
}