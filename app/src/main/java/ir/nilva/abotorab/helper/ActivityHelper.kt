package ir.nilva.abotorab.helper

import android.app.Activity
import ir.nilva.abotorab.*
import ir.nilva.abotorab.cabinet.CabinetActivity
import ir.nilva.abotorab.cabinet.CabinetListActivity
import ir.nilva.abotorab.webservices.cabinet.CabinetResponse
import org.jetbrains.anko.startActivity

/**
 * Created by mahdihs76 on 9/10/18.
 */

fun Activity.gotoMainPage() = startActivity<MainActivity>().apply { finish() }
fun Activity.gotoTakePage(barcode: String = "") = startActivity<TakeActivity>("barcode" to barcode)
fun Activity.gotoGivePage(barcode: String = "") = startActivity<GiveActivity>("barcode" to barcode)
fun Activity.gotoCabinetPage(cabinet: CabinetResponse? = null) = startActivity<CabinetActivity>("cabinet" to cabinet)
fun Activity.gotoCabinetListPage() = startActivity<CabinetListActivity>().apply { finish() }
fun Activity.gotoReportPage() = startActivity<ReportActivity>()
fun Activity.gotoBarcodePage(isQR: Boolean) = startActivity<BarcodeActivity>("isQR" to isQR).apply { finish() }


