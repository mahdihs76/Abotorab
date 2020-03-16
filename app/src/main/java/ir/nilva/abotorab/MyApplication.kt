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
        MicroblinkSDK.setLicenseKey("sRwAAAARaXIubmlsdmEuYWJvdG9yYWL8E5lLgnoRkcC3kO/pero0WmCfflBdx7xBkZLZYZhPEFndvIOeMHSc2cF3RKrjYhb9B7MdXcKAiStc06EAw1zCEqJGXxbqA4sig6Czm8XHvHTbvaPLCinaSvmuY/jfs/H/pWisiswaedUKVEFZLz21wM0Dx6pRuo3IPIqqTSv9+pTsjic9b0TzJSCchqTZSgcCwow+PhlDU8kKYP6N3G3EgqqTE+K46DhKVB/ZvjGZUob8voR7aeEwruDk6jzz5FZfI9JcwKi4MpW/9p4=", this)


        AppDatabase.getInstance()

        Toasty.Config.getInstance()
            .setToastTypeface(getAppTypeface())
            .setTextSize(14)
            .apply()
    }
}