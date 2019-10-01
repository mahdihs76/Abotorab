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
        MicroblinkSDK.setLicenseKey("sRwAAAARaXIubmlsdmEuYWJvdG9yYWL8E5lLgnoRkcC3kG/t7hKVsQb+d3yfAp8oMounLy/QsJBoqU1HiO1eeSEDfG5LmV6ZXdX1VmNhpFAJd0aNo6AskiuJ/1OvgUY3JaHc/53S8VfQeuuVMewXeCfOoJ6UeRkBD2V/CJKf7asOyA2iOq5kRgMZZZxlRHE+Du8QA/Gk8++bSsqkXanzfhE6KEMwhmrkvtNnoYRRBa+Q/mMA8r5a+mtyDYZWi9KC/jVwM2mmyCwG/YAVaL7CzK0NW5vb8ZEavw==", this);


        AppDatabase.getInstance()

        Toasty.Config.getInstance()
            .setToastTypeface(getAppTypeface())
            .setTextSize(14)
            .apply()
    }
}