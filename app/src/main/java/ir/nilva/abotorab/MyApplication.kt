package ir.nilva.abotorab

import androidx.multidex.MultiDexApplication
import com.microblink.MicroblinkSDK

/**
 *
 * Created by mahdihs76 on 9/11/18.
 */
class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        ApplicationContext.initialize(this)

        MicroblinkSDK.setLicenseKey("sRwAAAARaXIubmlsdmEuYWJvdG9yYWL8E5lLgnoRkcC3kG/trhiVnBsJr0UjXDf6MRCjF/kOqDVI4ngTdb2Oi2CUCfGNKkl4a7YKWASBilpP96OjbBYOGpX5xs0uk6PBPSROK6+ywVIaqK0L/tkR/9f4Q4Fwr049+YvjYLyM9s4KoAQqcvx3/laAO7jYtxQNZsVipCqaVl/6ZTubGC0Vztn10N+Kb2I+3IfHA8Lump/ovUhwi19oB2X72HTxmP+bMouKPvXsRxZ4WO+WXPd1NbtlOIjZ0NQdFA==", this)
    }
}