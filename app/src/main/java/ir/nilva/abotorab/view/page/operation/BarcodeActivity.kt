package ir.nilva.abotorab.view.page.operation

import android.graphics.PointF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.gotoGivePage
import ir.nilva.abotorab.helper.gotoTakePage
import kotlinx.android.synthetic.main.activity_barcode.*


class BarcodeActivity : AppCompatActivity(), QRCodeReaderView.OnQRCodeReadListener {

    private var isQR = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        qrView.setOnQRCodeReadListener(this)

        qrView.setQRDecodingEnabled(true)
        qrView.setTorchEnabled(true)

        isQR = intent?.extras?.getBoolean("isQR") ?: false
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        val result = text ?: ""
        if (!isQR) gotoTakePage(result) else gotoGivePage(result)
        finish()
    }

    override fun onResume() {
        super.onResume()
        qrView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        qrView.stopCamera()
    }

}
