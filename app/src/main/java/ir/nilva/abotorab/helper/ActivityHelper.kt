package ir.nilva.abotorab.helper

import android.app.Activity
import ir.nilva.abotorab.view.page.cabinet.CabinetActivity
import ir.nilva.abotorab.view.page.cabinet.CabinetListActivity
import ir.nilva.abotorab.view.page.main.MainActivity
import ir.nilva.abotorab.view.page.operation.BarcodeActivity
import ir.nilva.abotorab.view.page.operation.GiveActivity
import ir.nilva.abotorab.view.page.operation.ReportActivity
import ir.nilva.abotorab.view.page.operation.TakeActivity
import org.jetbrains.anko.startActivity

/**
 * Created by mahdihs76 on 9/10/18.
 */

fun Activity.gotoMainPage() = startActivity<MainActivity>().apply { finish() }
fun Activity.gotoTakePage(barcode: String = "") = startActivity<TakeActivity>("barcode" to barcode)
fun Activity.gotoGivePage(barcode: String = "") = startActivity<GiveActivity>("barcode" to barcode)
fun Activity.gotoCabinetPage(code: Int = -1) = startActivity<CabinetActivity>("code" to code)
fun Activity.gotoCabinetListPage() = startActivity<CabinetListActivity>()
fun Activity.gotoReportPage() = startActivity<ReportActivity>()
fun Activity.gotoBarcodePage(isQR: Boolean) = startActivity<BarcodeActivity>("isQR" to isQR).apply { finish() }



