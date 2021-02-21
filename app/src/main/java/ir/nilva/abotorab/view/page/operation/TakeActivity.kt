package ir.nilva.abotorab.view.page.operation

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.microblink.activity.DocumentScanActivity
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkid.passport.PassportRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.DocumentUISettings
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.callWebserviceWithFailure
import ir.nilva.abotorab.webservices.getServices
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

        val depoName = defaultCache()["depository"] ?: "امانت داری"
        depositoryName.setText(depoName)

        bagCount.minValue = 0
        bagCount.sideTapEnabled = true
        suitcaseCount.minValue = 0
        suitcaseCount.sideTapEnabled = true
        pramCount.minValue = 0
        pramCount.sideTapEnabled = true

        fab.setOnClickListener {
            startScanning()
        }

        bottom_app_bar.setNavigationOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                callWebservice {
                    getServices().deliveryListWithLimit(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        country.text.toString(),
                        phone.text.toString(),
                        passportId.text.toString(),
                        true,
                        5
                    )
                }?.run {
                    showResult("چاپ", this) {
                        CoroutineScope(Dispatchers.Main).launch {
                            callWebservice {
                                getServices().reprint(it)
                            }?.run {
                                toastSuccess("برگه تحویل با موفقیت چاپ شد")
                            }
                        }
                    }
                }
            }
        }

        recognizer = PassportRecognizer()
        recognizerBundle = RecognizerBundle(recognizer)

        submit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showLoading()
                callWebserviceWithFailure({
                    getServices().take(
                        firstName.text.toString(),
                        lastName.text.toString(), phone.text.toString(),
                        country.text.toString(), passportId.text.toString(),
                        bagCount.count, suitcaseCount.count, pramCount.count
                    )
                }) { response, code ->
                    if (response == "[\"فضای خالی وجود ندارد\"]") {
                        tryToFindEmptyCell()
                    } else {
                        toastError("درخواست شما با خطا روبه‌رو شد. مجدد تلاش کنید")
                    }
                }?.run {
                    resetUi()
                    toastSuccess("محموله با موفقیت تحویل گرفته شد")
                }
                hideLoading()
            }
        }

        country.threshold = 1
        country.setAdapter(CountryAdapter(this, R.layout.item_country, ArrayList(getCountries())))
    }

    private var tryToFindDialog: Dialog? = null
    var findEmptyCellEnabled = false

    private fun tryToFindEmptyCell() {
        findEmptyCellEnabled = true
        tryToFindDialog = MaterialDialog(this).show {
            customView(R.layout.try_to_find_empty_cell_dialog)
        }.cornerRadius(20f).negativeButton(text = "انصراف") {
            findEmptyCellEnabled = false
        }
        tryToFindDialog?.show()
        callApiAfter10Seconds()
    }

    private fun callApiAfter10Seconds() {
        Handler().postDelayed({
            CoroutineScope(Dispatchers.Main).launch {
                callWebserviceWithFailure({
                    getServices().take(
                        firstName.text.toString(),
                        lastName.text.toString(), phone.text.toString(),
                        country.text.toString(), passportId.text.toString(),
                        bagCount.count, suitcaseCount.count, pramCount.count
                    )
                }) { response, code ->
                    if (response == "[\"فضای خالی وجود ندارد\"]") {
                        if (findEmptyCellEnabled) callApiAfter10Seconds()
                    } else {
                        findEmptyCellEnabled = false
                        tryToFindDialog?.dismiss()
                        toastError("درخواست شما با خطا روبه‌رو شد. مجدد تلاش کنید")
                    }
                }?.run {
                    findEmptyCellEnabled = false
                    tryToFindDialog?.dismiss()
                    resetUi()
                    toastSuccess("محموله با موفقیت تحویل گرفته شد")
                }
            }
        }, 10000)
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
        bagCount.count = 0
        suitcaseCount.count = 0
        pramCount.count = 0

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
