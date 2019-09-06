package ir.nilva.abotorab.view.page.operation

import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.gotoBarcodePage
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.webservices.MyRetrofit
import ir.nilva.abotorab.model.DeliveryResponse
import ir.nilva.abotorab.view.page.base.BaseActivity
import kotlinx.android.synthetic.main.activity_give.*
import kotlinx.android.synthetic.main.activity_take.bottom_navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GiveActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_give)


        bottom_navigation.addItem(AHBottomNavigationItem("دوربین", android.R.drawable.ic_menu_camera))
        bottom_navigation.defaultBackgroundColor = Color.parseColor("#0E4C59")
        bottom_navigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottom_navigation.accentColor = Color.parseColor("#00E990")
        bottom_navigation.inactiveColor = Color.parseColor("#00E990")

        bottom_navigation.setOnTabSelectedListener { _, _ ->
            gotoBarcodePage(true)
            true
        }

        search.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {

                    val response = MyRetrofit.getService().deliveryList(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        country.text.toString(),
                        phone.text.toString(),
                        passportId.text.toString()
                    )
                    if (response.isSuccessful) {
                        showSearchResult(response.body())
                    } else toastError(response.toString())
                } catch (e: Exception){ toastError(e.message.toString())}
            }
        }

        val extras = intent.extras
        if (extras != null && extras.containsKey("barcode")) {
            val barcode = extras.getString("barcode")
            if (!barcode.isNullOrEmpty()) {
                val data = Base64.decode(barcode.toString(), Base64.DEFAULT)
                val text = String(data, Charsets.UTF_8)
                val splitted = text.split("#")
                val firstName = splitted[0]
                val lastName = splitted[1]
                val lastPhoneDigits = splitted[2]
                val hashId = splitted[3]
                MaterialDialog(this).show {
                    title(text = "تایید")
                    message(text = "محموله به $firstName $lastName تحویل داده شود؟ ")
                    positiveButton(text = "بله") { callGiveWS(hashId) }
                    negativeButton(text = "خیر")
                }
            }
        }
    }

    private fun showSearchResult(list: List<DeliveryResponse>?) {
        list ?: return
        val view = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            orientation = VERTICAL
        }
        list.forEach { item ->
            view.addView(TextView(this).apply {
                text = "zaer ${item.giver}"
                setOnClickListener {
                    callGiveWS(item.hashId)
                }
            })
        }
        MaterialDialog(this).show {
            customView(view = view)
        }
    }

    private fun callGiveWS(hashId: String) = CoroutineScope(Dispatchers.Main).launch {
        try {
            MyRetrofit.getService().give(hashId)
            toastSuccess("محموله با موفقیت تحویل داده شد")
        } catch (e: Exception) {
            toastError(e.message.toString())
        }
    }

}
