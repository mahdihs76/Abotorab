package ir.nilva.abotorab.view.page.operation

import android.graphics.PointF
import android.os.Bundle
import android.util.Base64
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.android.synthetic.main.give_verification.view.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange


class CameraActivity : BaseActivity(), QRCodeReaderView.OnQRCodeReadListener {

    private var isQR = false
    private var mostRecentHashId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        confirm_cb.isChecked = defaultCache()["need_to_confirmation"] ?: true

        confirm_cb.onCheckedChange { _, isChecked ->
            defaultCache()["need_to_confirmation"] = isChecked
        }

        confirm_layout.setOnClickListener { confirm_cb.performClick() }
        recent_layout.setOnClickListener { recent.performClick() }
        search_layout.setOnClickListener { search.performClick() }

        search.setOnClickListener { gotoGiveSearchPage() }
        recent.setOnClickListener { gotoRecentGivesPage() }

        qrView.setOnQRCodeReadListener(this)

        qrView.setQRDecodingEnabled(true)
        qrView.setTorchEnabled(true)

        isQR = intent?.extras?.getBoolean("isQR") ?: false
    }

    var isBarcodeFound = false

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        if (!isBarcodeFound) {
            isBarcodeFound = true
            val result = text ?: ""
            give(result)
        }
    }

    private fun give(barcode: String) {
        if (barcode.isNotEmpty()) {
            try {
                val encodedString = barcode.substringAfter("http://gbaghiyatallah.ir/?data=");
                val text = String(
                    Base64.decode(
                        encodedString,
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

                confirmToGive(view, hashId, text[4])
            } catch (e: Exception) {
                toastError("بارکد اسکن شده معتبر نمی‌باشد")
            }
        }
    }

    private fun confirmToGive(view: View?, hashId: String, cellCode: String) {
        if (mostRecentHashId == hashId) {
            isBarcodeFound = false
            return
        }
        val need = defaultCache()["need_to_confirmation"] ?: true
        if (need) {
            MaterialDialog(this).show {
                customView(view = view)
                title(text = "تایید")
                positiveButton(text = "بله") {
                    callGiveWS(hashId, cellCode)
                    mostRecentHashId = hashId
                    isBarcodeFound = false
                }
                negativeButton(text = "خیر") {
                    isBarcodeFound = false
                    mostRecentHashId = null
                }
            }
        } else {
            callGiveWS(hashId, cellCode)
            mostRecentHashId = hashId
            isBarcodeFound = false
        }
    }

    override fun onResume() {
        super.onResume()
        qrView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        isBarcodeFound = false
        mostRecentHashId = null
        qrView.stopCamera()
    }

}
