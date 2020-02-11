package ir.nilva.abotorab

import androidx.multidex.MultiDexApplication
import com.microblink.MicroblinkSDK
import es.dmoral.toasty.Toasty
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.helper.getAppTypeface

/**
 *
 * Created by mahdihs76 on 9/11/18.
 */
class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        ApplicationContext.initialize(this)

        /** Set the Base64 license */
        MicroblinkSDK.setLicenseKey("sRwAAAARaXIubmlsdmEuYWJvdG9yYWL8E5lLgnoRkcC3kO/pWr40fK2eSJur0wv3+z0r6BWGMk/GdB1rzTiytkDbtZjwj8KaJvjS2sjrkSUm4iyEXsDRwhCgg1IW7IB22PaBbc3qO66Kg0yNMhP/UKI21TZbCP88flq2GZ+/0G7HldRN6VJemntWtPJPQOq3wqm91Hewc6bXM1BWM7M47b3xN5Dp4am/qqm4ZfQRXwpfR/zQ7v6Udr4Gvn0VAMYVQtf2wHZNUcTNzSC+h4kgs+hjYQrsHNphpWsrW4NjoBury8Q=", this);


        AppDatabase.getInstance()

        Toasty.Config.getInstance()
            .setToastTypeface(getAppTypeface())
            .setTextSize(14)
            .apply()
    }
}