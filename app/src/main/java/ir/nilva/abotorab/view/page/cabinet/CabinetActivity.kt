package ir.nilva.abotorab.view.page.cabinet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.zhouwei.library.CustomPopWindow
import com.github.florent37.viewanimator.ViewAnimator
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.model.CabinetResponse
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import kotlinx.android.synthetic.main.activity_cabinet.*
import kotlinx.android.synthetic.main.cabinet_popup.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.dionsegijn.steppertouch.OnStepCallback
import org.jetbrains.anko.imageResource

class CabinetActivity : BaseActivity() {

    private var rows = 1
    private var columns = 1
    private var adapter = CabinetAdapter(null, rows, columns)
    private var step = 0
    private lateinit var popup: CustomPopWindow
    private lateinit var currentCabinet: CabinetResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabinet)

        val cabinet = intent?.extras?.getSerializable("cabinet") as CabinetResponse?
        if (cabinet != null) {
            rows = cabinet.getRowsNumber()
            columns = cabinet.getColumnsNumber()
            currentCabinet = cabinet
            adapter.cabinet = currentCabinet
            moveToNextStep(false)
        }

        rowsCount.addStepCallback(object : OnStepCallback {
            override fun onStep(value: Int, positive: Boolean) = updateRows(value)
        })

        columnsCount.addStepCallback(object : OnStepCallback {
            override fun onStep(value: Int, positive: Boolean) = updateColumns(value)
        })

        rowsCount.count = rows
        columnsCount.count = columns

        rowsCount.minValue = 1
        columnsCount.minValue = 1

        rowsCount.maxValue = 10
        columnsCount.maxValue = 10

        grid.numColumns = columns
        grid.adapter = adapter

        grid.setOnItemClickListener { _, view, index, _ ->
            if (step == 0) return@setOnItemClickListener
            showPopup(
                view,
                index
            )
        }

        submit.setOnClickListener {
            if (step == 0) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        currentCabinet = MyRetrofit.getInstance().webserviceUrls
                            .cabinet("", rows, columns, 1, 1)
                            .body()!!
                        adapter.cabinet = currentCabinet
                        moveToNextStep(true)
                    } catch (e: Exception) {
                        toastError(e.message.toString())
                    }
                }
            }
        }

    }

    private fun moveToNextStep(withAnimation: Boolean) {
        step++
        if (withAnimation) {
            ViewAnimator
                .animate(grid)
                .translationY(-(steppers.height.toFloat() + labels.height.toFloat()))
                .andAnimate(steppers)
                .alpha(0F)
                .andAnimate(labels)
                .alpha(0F)
                .andAnimate(submit)
                .translationY(submit.height.toFloat())
                .andAnimate(subHeader)
                .alpha(0F)
                .andAnimate(secondSubHeader)
                .alpha(1F)
                .start()
        } else {
            steppers.visibility = View.GONE
            labels.visibility = View.GONE
            submit.visibility = View.GONE
            subHeader.alpha = 0F
            secondSubHeader.alpha = 1F
        }
        header.text = String.format(
            getString(R.string.cabinet_format),
            currentCabinet.code.toString()
        )
    }

    fun updateRows(value: Int) {
        rows = value
        adapter.rows = rows
        adapter.notifyDataSetChanged()
    }

    fun updateColumns(value: Int) {
        columns = value
        grid.numColumns = columns
        adapter.columns = columns
        adapter.notifyDataSetChanged()
    }

    private fun showPopup(view: View, index: Int) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.cabinet_popup, null)
        if (currentCabinet.getCell(index).isHealthy) {
            popupView.layout1.setOnClickListener {
                popup.dissmiss()
                changeStatus(view, index, false)
            }
            popupView.text1.text = "غیر قابل استفاده"
            popupView.image1.imageResource = R.mipmap.error
        } else {
            popupView.layout1.setOnClickListener {
                popup.dissmiss()
                changeStatus(view, index, true)
            }
            popupView.text1.text = "قابل استفاده"
            popupView.image1.imageResource = R.mipmap.success
        }
        popup = CustomPopWindow.PopupWindowBuilder(this)
            .setView(popupView)
            .size(500, 300)
            .setFocusable(true)
            .setOutsideTouchable(true)
            .create()
            .showAsDropDown(view, 0, 10)
    }

    private fun changeStatus(view: View, index: Int, isHealthy: Boolean) =
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val cell = currentCabinet.getCell(index)
                MyRetrofit.getService().changeStatus(
                    cell.code,
                    isHealthy
                )
                cell.isHealthy = isHealthy
                ViewAnimator.animate(view)
                    .alpha(if (isHealthy) 1F else 0.3F)
                    .start()
            } catch (e: Exception) {
                toastError(e.message.toString())
            }
        }

    override fun onBackPressed() {
        super.onBackPressed()
        gotoCabinetListPage()
    }

}
