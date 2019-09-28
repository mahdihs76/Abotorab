package ir.nilva.abotorab.view.page.cabinet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import com.example.zhouwei.library.CustomPopWindow
import com.github.florent37.viewanimator.ViewAnimator
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.helper.getCell
import ir.nilva.abotorab.helper.getColumnsNumber
import ir.nilva.abotorab.helper.getRowsNumber
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.model.CabinetResponse
import ir.nilva.abotorab.model.Cell
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
        initUi()

        val code = intent?.extras?.getInt("code")
        observeOnDb(code)
    }

    private fun observeOnDb(code: Int?) {
        if (code != null && code != -1) {
            AppDatabase.getInstance().cabinetDao().get(code).observe(this, Observer {
                rows = it.getRowsNumber()
                columns = it.getColumnsNumber()
                currentCabinet = it
                adapter.cabinet = currentCabinet
                moveToNextStep(false)
                refresh()
            })
        }
    }

    private fun initUi() {
        initSteppers()
        initGrid()
        submit.setOnClickListener { addCabinet() }
    }

    private fun addCabinet() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                submit.isClickable = false
                currentCabinet = MyRetrofit.getInstance().webserviceUrls
                    .cabinet("", rows, columns, 1, 1)
                    .body()!!
                AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
                adapter.cabinet = currentCabinet
                moveToNextStep(true)
                observeOnDb(currentCabinet.code)
            } catch (e: Exception) {
                toastError(e.message.toString())
            }
        }
    }

    private fun initGrid() {
        grid.adapter = adapter
        grid.setOnItemClickListener { _, view, index, _ ->
            if (step == 0) return@setOnItemClickListener
            showPopup(view, index)
        }
    }

    private fun initSteppers() {
        rowsCount.addStepCallback(object : OnStepCallback {
            override fun onStep(value: Int, positive: Boolean) = updateRows(value)
        })

        columnsCount.addStepCallback(object : OnStepCallback {
            override fun onStep(value: Int, positive: Boolean) = updateColumns(value)
        })

        rowsCount.minValue = 1
        columnsCount.minValue = 1
        rowsCount.maxValue = 10
        columnsCount.maxValue = 10
        rowsCount.sideTapEnabled = true
        columnsCount.sideTapEnabled = true

        rowsCount.count = rows
        columnsCount.count = columns
    }

    private fun refresh() {
        rowsCount.count = rows
        columnsCount.count = columns
        grid.numColumns = columns
    }

    private fun moveToNextStep(withAnimation: Boolean) {
        if (step == 1) return
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

        val cell = currentCabinet.getCell(index)
        popupView.cabinetCode.text = "شماره " + cell.code
        if (cell.age > -1) {
            popupView.layout1.visibility = View.GONE
        }
        if (cell.isHealthy) {
            popupView.layout1.setOnClickListener {
                popup.dissmiss()
                changeStatus(index, false)
            }
            popupView.text1.text = "غیر قابل استفاده"
            popupView.image1.imageResource = R.mipmap.error
        } else {
            popupView.layout2.visibility = View.GONE
            popupView.layout1.setOnClickListener {
                popup.dissmiss()
                changeStatus(index, true)
            }
            popupView.text1.text = "قابل استفاده"
            popupView.image1.imageResource = R.mipmap.success
        }
        popupView.layout2.setOnClickListener {
            popup.dissmiss()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = MyRetrofit.getService().favorite(cell.code.toInt())
                    if (response.isSuccessful) {
                        getPrevFavorite()?.isFavorite = false
                        cell.isFavorite = true
                        AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
                    } else toastError("Error")
                } catch (e: Exception) {
                    toastError("Error")
                }
            }
        }
        if (cell.age == 1) {
            popupView.layout3.visibility = View.VISIBLE
        }

        popupView.layout3.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                MyRetrofit.getService().deliverToStore(cell.code.toInt())
                cell.age = -1
                AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
            }
        }

        popup = CustomPopWindow.PopupWindowBuilder(this)
            .setView(popupView)
            .size(500, 300)
            .setFocusable(true)
            .setOutsideTouchable(true)
            .create()
            .showAsDropDown(view, 0, 10)
    }

    private fun getPrevFavorite(): Cell? {
        for (row in currentCabinet.rows) {
            for (cell in row.cells) {
                if (cell.isFavorite) return cell
            }
        }
        return null
    }

    private fun changeStatus(index: Int, isHealthy: Boolean) =
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val cell = currentCabinet.getCell(index)
                MyRetrofit.getService().changeStatus(cell.code, isHealthy)
                cell.isHealthy = isHealthy
                AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
            } catch (e: Exception) {
                toastError(e.message.toString())
            }
        }

}
