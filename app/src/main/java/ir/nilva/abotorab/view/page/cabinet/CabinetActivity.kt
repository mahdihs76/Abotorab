package ir.nilva.abotorab.view.page.cabinet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment
import com.commit451.modalbottomsheetdialogfragment.Option
import com.commit451.modalbottomsheetdialogfragment.OptionRequest
import com.example.zhouwei.library.CustomPopWindow
import com.github.florent37.viewanimator.ViewAnimator
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.helper.getCell
import ir.nilva.abotorab.helper.getColumnsNumber
import ir.nilva.abotorab.helper.getRowsNumber
import ir.nilva.abotorab.helper.gotoFullScreenPage
import ir.nilva.abotorab.model.CabinetResponse
import ir.nilva.abotorab.model.Cell
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.activity_cabinet.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.dionsegijn.steppertouch.OnStepCallback

const val UNUSABLE_OPTION_ID = 1
const val USABLE_OPTION_ID = 2
const val EMPTY_OPTION_ID = 3
const val FAV_OPTION_ID = 4
const val STORE_OPTION_ID = 5
const val PRINT_OPTION_ID = 6

class CabinetActivity : BaseActivity(), ModalBottomSheetDialogFragment.Listener {

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
                it ?: return@Observer
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
        fullScreen.setOnClickListener { gotoFullScreenPage(currentCabinet.code) }
    }

    private fun addCabinet() {
        CoroutineScope(Dispatchers.Main).launch {
            submit.isClickable = false
            callWebservice {
                getServices().cabinet("", rows, columns, 1, 1)
            }?.run {
                currentCabinet = this
                AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
                adapter.cabinet = currentCabinet
                moveToNextStep(true)
                observeOnDb(currentCabinet.code)
            }
        }
    }

    private fun initGrid() {
        grid.adapter = adapter
        grid.setOnItemClickListener { _, view, index, _ ->
            if (step == 0) return@setOnItemClickListener
            showPopup(index)
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
                .andAnimate(fullScreen)
                .alpha(1F)
                .start()
        } else {
            steppers.visibility = View.GONE
            labels.visibility = View.GONE
            submit.visibility = View.GONE
            subHeader.alpha = 0F
            secondSubHeader.alpha = 1F
            fullScreen.alpha = 1F
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

    private fun ModalBottomSheetDialogFragment.Builder.addOptions(cell: Cell, index: Int) {
        if (cell.age <= -1 && cell.isHealthy) {
            add(
                OptionRequest(
                    (index.toString() + UNUSABLE_OPTION_ID).toInt(),
                    getString(R.string.unusable),
                    R.drawable.round_visibility_off_black_24
                )
            )
        } else if (cell.age <= -1) {
            add(
                OptionRequest(
                    (index.toString() + USABLE_OPTION_ID).toInt(),
                    getString(R.string.usable),
                    R.drawable.round_visibility_black_24
                )
            )
        }
        if (cell.age > -1) {
            add(
                OptionRequest(
                    (index.toString() + EMPTY_OPTION_ID).toInt(),
                    getString(R.string.empty),
                    R.drawable.round_move_to_inbox_black_24
                )
            )
        }

        if (cell.isHealthy) {
            add(
                OptionRequest(
                    (index.toString() + FAV_OPTION_ID).toInt(),
                    getString(R.string.favorite),
                    R.drawable.round_favorite_black_24
                )
            )
        }
        if (cell.age == 1) {
            add(
                OptionRequest(
                    (index.toString() + STORE_OPTION_ID).toInt(),
                    getString(R.string.store),
                    R.drawable.round_store_black_24
                )
            )
        }
        add(
            OptionRequest(
                (index.toString() + PRINT_OPTION_ID).toInt(),
                getString(R.string.print),
                R.drawable.baseline_print_black_24
            )
        )
    }

    private fun showPopup(index: Int) {

        val cell = currentCabinet.getCell(index)

        val modalBuilder =
            ModalBottomSheetDialogFragment.Builder()
                .header(getHeaderTitle(cell))

        modalBuilder.addOptions(cell, index)

        modalBuilder.show(supportFragmentManager, "bottomsheet")

    }

    private fun getHeaderTitle(cell: Cell) =
        if (cell.pilgrim?.name.isNullOrEmpty()) String.format("شماره : %s", cell.code)
        else String.format("شماره : %s - رزرو شده توسط %s", cell.code, cell.pilgrim?.name ?: "")


    override fun onModalOptionSelected(tag: String?, option: Option) {
        val cellIndex = option.id / 10
        val cell = currentCabinet.getCell(cellIndex)
        when (option.id % 10) {
            UNUSABLE_OPTION_ID -> changeStatus(cellIndex, false)
            USABLE_OPTION_ID -> changeStatus(cellIndex, true)
            EMPTY_OPTION_ID -> CoroutineScope(Dispatchers.Main).launch {
                callWebservice { getServices().free(currentCabinet.getCell(cellIndex).code.toInt()) }?.run {
                    cell.age = -1
                    AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
                }
            }
            FAV_OPTION_ID -> CoroutineScope(Dispatchers.Main).launch {
                callWebservice { getServices().favorite(cell.code.toInt()) }?.run {
                    getPrevFavorite()?.isFavorite = false
                    cell.isFavorite = true
                    AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
                }
            }
            STORE_OPTION_ID -> CoroutineScope(Dispatchers.Main).launch {
                callWebservice { getServices().deliverToStore(cell.code.toInt()) }?.run {
                    cell.age = -1
                    AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
                }
            }
            PRINT_OPTION_ID -> CoroutineScope(Dispatchers.Main).launch {
                callWebservice { getServices().print(cell.code.toInt()) }
            }
        }
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
            val cell = currentCabinet.getCell(index)
            callWebservice {
                getServices().changeStatus(cell.code, isHealthy)
            }?.run {
                cell.isHealthy = isHealthy
                AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
            }
        }
}
