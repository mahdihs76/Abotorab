package ir.nilva.abotorab.view.page.operation

import android.os.Bundle
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.barcode.Barcode
import info.androidhive.barcode.BarcodeReader
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.gotoGivePage
import ir.nilva.abotorab.helper.gotoTakePage
import ir.nilva.abotorab.helper.toastError


class BarcodeActivity : AppCompatActivity(), BarcodeReader.BarcodeReaderListener {

    private lateinit var barcodeReader: BarcodeReader
    private var isQR = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)
        barcodeReader = supportFragmentManager.findFragmentById(R.id.barcode_fragment) as BarcodeReader
        isQR = intent?.extras?.getBoolean("isQR") ?: false
    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>?) {

    }

    override fun onScannedMultiple(barcodes: MutableList<Barcode>?) {

    }

    override fun onCameraPermissionDenied() {

    }

    override fun onScanned(barcode: Barcode?) {
        barcodeReader.playBeep()
        val result = barcode?.rawValue ?: ""
        if (!isQR) gotoTakePage(result) else gotoGivePage(result)
        finish()
    }

    override fun onScanError(errorMessage: String?) {
        toastError(getString(R.string.camera_permission_warning))
        onBackPressed()
        finish()

    }
}
