package ir.nilva.abotorab.view.page.main

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.db.model.DeliveryEntity
import ir.nilva.abotorab.db.model.OfflineDeliveryEntity
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.callWebserviceWithFailure
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.accounting_main.*
import kotlinx.android.synthetic.main.give_verification.view.*
import kotlinx.android.synthetic.main.progress_dialog_material.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import permissions.dispatcher.*

@RuntimePermissions
class LoginActivity : BaseActivity() {

    @NeedsPermission(Manifest.permission.CAMERA)
    fun scanVisitCard() {
        gotoBarcodePage(false)
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showDialogBeforeCameraPermission(request: PermissionRequest) {
        MaterialDialog(this).show {
            message(R.string.camera_permission_message)
            positiveButton(R.string.permisson_ok) { request.proceed() }
            negativeButton(R.string.permission_deny) { request.cancel() }
        }
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        toast(getString(R.string.no_camera_permission))
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
        toast(getString(R.string.no_camera_permission))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accounting_main)

        ip.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "آدرس سرور جدید را وارد کنید")
                input(hint = "مثلا: 192.168.100.5", prefill = MyRetrofit.getBaseUrl()) { _, text ->
                    connect2Server(text.toString())
                }
                positiveButton(text = "اتصال")
            }
        }

        automaticIp.setOnClickListener {
            val dialog = MaterialDialog(this).show {
                customView(R.layout.progress_dialog_material)
            }
            CoroutineScope(Dispatchers.Main).launch {
                connectAutomatic()
                val customView = dialog.getCustomView()
                customView.progress.visibility = View.GONE
                customView.message.text = "اتصال به سرور با موفقیت انجام شد"
                Handler().postDelayed({
                    dialog.dismiss()
                }, 1000)
            }
        }
        submit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showLoading()
                callWebservice {
                    getServices().login(
                        username.text.toString(),
                        password.text.toString()
                    )
                }?.run {
                    defaultCache()["token"] = token
                    MyRetrofit.newInstance()
                    gotoMainPage()
                }
                hideLoading()
            }

        }

        scan.setOnClickListener { scanVisitCardWithPermissionCheck() }


        val extras = intent.extras
        if (extras != null && extras.containsKey("barcode")) {
            val barcode = extras.getString("barcode")
            if (!barcode.isNullOrEmpty()) {
                try {
                    val text = String(
                        Base64.decode(
                            barcode.toString(),
                            Base64.DEFAULT
                        ),
                        Charsets.UTF_8
                    ).split("#")

                    val hashId = text[2]
                    val view = layoutInflater.inflate(R.layout.give_verification, null)
                    view.nickName.text = text[0]
                    view.county.text = "از کشور ${text[1]}"
                    view.phoneNumber.text = "شماره تلفن‌ : ${text[3]}" + "********"
                    view.cellCode.text = "شماره قفسه : ${text[4]}"
                    MaterialDialog(this).show {
                        customView(view = view)
                        title(text = "تایید")
                        positiveButton(text = "بله") { callGiveWS(hashId) }
                        negativeButton(text = "خیر")
                    }
                } catch (e: Exception) {
                    toastError("بارکد اسکن شده معتبر نمیباشد")
                }
            }
        }

        val token: String? = defaultCache()["token"]
        if (token != null) gotoMainPage()

    }
    private fun callGiveWS(hashId: String) = CoroutineScope(Dispatchers.Main).launch {
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


    private fun connect2Server(ip: String) {
        MyRetrofit.setBaseUrl(ip)
    }

    private suspend fun connectAutomatic() {
        val validIps = arrayOf(
            "https://192.168.0.11/",
            "http://depository.ketaabkhaane.ir/",
            "https://192.168.0.12/",
            "https://192.168.0.13/",
            "https://192.168.0.14/",
            "https://192.168.0.15/",
            "https://192.168.0.16/"
        )

        for (ip in validIps) {
            connect2Server(ip)
            try {
                getServices().test()
                break
            } catch (e: Exception) {
            }
        }
    }

    private fun showLoading() {
        submit.visibility = View.INVISIBLE
        spinKit.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        submit.visibility = View.VISIBLE
        spinKit.visibility = View.GONE
    }
}
