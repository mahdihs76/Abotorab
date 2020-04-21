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
        MicroblinkSDK.setLicenseKey("sRwAAAARaXIubmlsdmEuYWJvdG9yYWL8E5lLgnoRkcC3kO/tyhWdEIvXOHOP2helVqsNg7YJo6AuYpfbA/73WhedSm7r+awfPb/bYrhjkn+OJUhA7kFknTtbB03eBQQAI+EqFUtPiScbN8v6cRbMqfrx/TXsiniVt7HmP6xm/1+FXTC2t6+C5lu1gkSQ0V6lqB2f2TlAJ4GFAID7JVUrDKn7ekD1iWlYRDvn/XfcvJS64MmicWZldxuchOwYc/cUeh1SqJ3KJ+7b32adv1G4bfPy1KjAj7lv6iA0FL+zk17XX5IFmaw=", this)

        AppDatabase.getInstance()

        Toasty.Config.getInstance()
            .setToastTypeface(getAppTypeface())
            .setTextSize(14)
            .apply()
    }
}