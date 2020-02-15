package ir.nilva.abotorab.view.page.operation

import android.content.Context
import android.os.Bundle
import android.view.View
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.db.model.DeliveryEntity
import ir.nilva.abotorab.db.model.OfflineDeliveryEntity
import ir.nilva.abotorab.helper.getCountries
import ir.nilva.abotorab.helper.showSearchResult
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.callWebserviceWithFailure
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.activity_give.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GiveSearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_give)

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
                    showSearchResult(this) {
                        callGiveWS(it)
                    }
                }
                search.visibility = View.VISIBLE
                spinKit.visibility = View.INVISIBLE
            }
        }

        country.threshold = 1
        country.setAdapter(CountryAdapter(this, R.layout.item_country, ArrayList(getCountries())))


    }

}

fun Context.callGiveWS(hashId: String) = CoroutineScope(Dispatchers.Main).launch {
    callWebserviceWithFailure({ getServices().give(hashId) }) {
        toastSuccess("پس از برقراری ارتباط با سرور گزارش میشود")
        cacheHashId(hashId)
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

