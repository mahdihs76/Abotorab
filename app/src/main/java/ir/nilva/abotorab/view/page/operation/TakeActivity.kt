package ir.nilva.abotorab.view.page.operation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.microblink.activity.DocumentScanActivity
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkid.passport.PassportRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.DocumentUISettings
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.getCountryName
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import kotlinx.android.synthetic.main.activity_take.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TakeActivity : BaseActivity() {

    private lateinit var recognizer: PassportRecognizer
    private lateinit var recognizerBundle: RecognizerBundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take)

        bagCount.minValue = 0
        suitcaseCount.minValue = 0
        pramCount.minValue = 0

        bottom_navigation.addItem(AHBottomNavigationItem("دوربین", android.R.drawable.ic_menu_camera))
        bottom_navigation.defaultBackgroundColor = Color.parseColor("#0E4C59")
        bottom_navigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottom_navigation.accentColor = Color.parseColor("#00E990")
        bottom_navigation.inactiveColor = Color.parseColor("#00E990")

        bottom_navigation.setOnTabSelectedListener { _, _ ->
            startScanning()
            true
        }

        recognizer = PassportRecognizer()
        recognizerBundle = RecognizerBundle(recognizer)

        submit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showLoading()
                try {
                    val response = MyRetrofit.getService().take(
                        firstName.text.toString(),
                        lastName.text.toString(), phone.text.toString(),
                        country.text.toString(), passportId.text.toString(),
                        bagCount.count, suitcaseCount.count, pramCount.count
                    )
                    hideLoading()
                    if (response.isSuccessful) {
                        toastSuccess("محموله با موفقیت تحویل گرفته شد")
                    } else toastError(response.errorBody()?.string()?:"")
                    resetUi()
                } catch (e: Exception) {
                    hideLoading()
                    toastError(e.message.toString())
                }

            }
        }

    }

    private fun startScanning() {
        val settings = DocumentUISettings(recognizerBundle)
        ActivityRunner.startActivityForResult(this, 100, settings)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if (resultCode == DocumentScanActivity.RESULT_OK && data != null) {
                recognizerBundle.loadFromIntent(data)
                val result = recognizer.result
                if (result.resultState == Recognizer.Result.State.Valid) {
                    val passport = result.mrzResult
                    firstName.setText(passport.secondaryId)
                    lastName.setText(passport.primaryId)
                    passportId.setText(passport.documentNumber)
                    country.setText(getCountryName(passport.nationality))
                }
            }
        }
    }

    private fun resetUi() {
        firstName.setText("")
        lastName.setText("")
        phone.setText("")
        country.setText("")
        passportId.setText("")

        firstName.requestFocus()
    }

    private fun showLoading() {
        spinKit.visibility = View.VISIBLE
        submit.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        spinKit.visibility = View.GONE
        submit.visibility = View.VISIBLE
    }
}
