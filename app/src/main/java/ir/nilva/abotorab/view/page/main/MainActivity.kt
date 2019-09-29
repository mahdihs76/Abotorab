package ir.nilva.abotorab.view.page.main

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.ramotion.circlemenu.CircleMenuView
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import ir.nilva.abotorab.work.DeliveryWorker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCircularMenu()
        logout.setOnClickListener { logout() }
        sendCachedHashes2Server()
        fillHeader()
    }

    private fun fillHeader() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = MyRetrofit.getService().startReport()
                if (response.isSuccessful) {
                    val startReport = response.body() ?: return@launch
                    cabinetCount.text = startReport.totalCabinets.toString()
                    emptyCabinets.text = startReport.emptyCells.toString()
                    registerCount.text = startReport.totalDeliveries.toString()
                }
            } catch (e: Exception) { }
        }
    }

    private fun sendCachedHashes2Server() {
        CoroutineScope(Dispatchers.Main).launch {
            val offlineDeliveries =
                AppDatabase.getInstance().offlineDeliveryDao().getAll()
            if (!offlineDeliveries.isNullOrEmpty()) {
                WorkManager.getInstance(this@MainActivity).enqueue(
                    OneTimeWorkRequestBuilder<DeliveryWorker>()
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                        .build()
                )
            }
        }
    }

    private fun initCircularMenu() {
        circularLayout.eventListener = object : CircleMenuView.EventListener() {
            override fun onButtonClickAnimationEnd(view: CircleMenuView, buttonIndex: Int) {
                super.onButtonClickAnimationEnd(view, buttonIndex)
                getMenuItem(buttonIndex)?.action(this@MainActivity)
            }
        }

        openMenu()
    }

    private fun openMenu() {
        Handler().postDelayed({
            circularLayout.open(true)
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        openMenu()
    }

}

fun MenuItem.action(activity: Activity) {
    when (this) {
        MenuItem.CABINET_GIVE -> activity.gotoGivePage()
        MenuItem.CABINET_TAKE -> activity.gotoTakePage()
        MenuItem.CABINET_INIT -> activity.gotoCabinetListPage()
        MenuItem.CABINET_REPORT -> activity.gotoReportPage()
    }
}

fun Activity.logout() {
    MaterialDialog(this).show {
        title(text = "آیا برای خروج اطمینان دارید؟")
        positiveButton(text = "بله") {
            CoroutineScope(Dispatchers.Main).launch {
                AppDatabase.getInstance().deliveryDao().clear()
                AppDatabase.getInstance().cabinetDao().clear()
                AppDatabase.getInstance().offlineDeliveryDao().clear()
                defaultCache()["token"] = null
                finish()
            }
        }
        negativeButton(text = "خیر")
    }
}


