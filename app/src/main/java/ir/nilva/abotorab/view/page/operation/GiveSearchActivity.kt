package ir.nilva.abotorab.view.page.operation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.microblink.activity.DocumentScanActivity
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkid.passport.PassportRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.DocumentUISettings
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.db.model.DeliveryEntity
import ir.nilva.abotorab.db.model.OfflineDeliveryEntity
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.callWebserviceWithFailure
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.activity_give.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GiveSearchActivity : BaseActivity() {

    private lateinit var recognizer: PassportRecognizer
    private lateinit var recognizerBundle: RecognizerBundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_give)

        recognizer = PassportRecognizer()
        recognizerBundle = RecognizerBundle(recognizer)

        search.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                search.visibility = View.INVISIBLE
                spinKit.visibility = View.VISIBLE
                callWebservice {
                    getServices().deliveryList(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        country.text.toString(),
                        phone.text.toString(),
                        passportId.text.toString(),
                        true
                    )
                }?.run {
                    showResult("تحویل", this) {
                        callGiveWS(it)
                    }
                }
                search.visibility = View.VISIBLE
                spinKit.visibility = View.INVISIBLE
            }
        }

        country.threshold = 1
        country.setAdapter(CountryAdapter(this, R.layout.item_country, ArrayList(getCountries())))

        fab.setOnClickListener {
            startScanning()
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

}



fun Context.callGiveWS(hashId: String) = CoroutineScope(Dispatchers.Main).launch {
    callWebserviceWithFailure({ getServices().give(hashId) }) {
        toastError(it)
//        toastSuccess("پس از برقراری ارتباط با سرور گزارش میشود")
//        TODO: If there is no internet connection then insert into cacheHashId.
//        cacheHashId(hashId)
    }?.run {
        toastSuccess("محموله با موفقیت تحویل داده شد")
        AppDatabase.getInstance().deliveryDao().insertAndDeleteOther(
            DeliveryEntity(
                nickname = pilgrim.name,
                country = pilgrim.country,
                phone = pilgrim.phone,
                exitedAt = exitAt,
                hashId = hashId
            )
        )
    }
}

private fun cacheHashId(hashId: String) = CoroutineScope(Dispatchers.IO).launch {
    AppDatabase.getInstance().offlineDeliveryDao().insert(OfflineDeliveryEntity(hashId))
}

