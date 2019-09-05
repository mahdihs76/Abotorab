package ir.nilva.abotorab

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by mahdihs76 on 9/10/18.
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCalligraphy()
    }

    private fun initCalligraphy() =
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/yekan.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

    override fun attachBaseContext(newBase: Context) =
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))

}