package ir.nilva.abotorab.view.page.cabinet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment
import com.commit451.modalbottomsheetdialogfragment.Option
import com.commit451.modalbottomsheetdialogfragment.OptionRequest
import com.github.florent37.viewanimator.ViewAnimator
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.helper.*
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
import nl.dionsegijn.steppertouch.StepperTouch

const val UNUSABLE_OPTION_ID = 1
const val USABLE_OPTION_ID = 2
const val EMPTY_OPTION_ID = 3
const val FAV_OPTION_ID = 4
const val STORE_OPTION_ID = 5
const val PRINT_OPTION_ID = 6

class CabinetActivity : BaseActivity(), ModalBottomSheetDialogFragment.Listener {

    private var rows = 3
    private var columns = 4
    private var dir = 0
    private var carriageEnabled = false
    private var adapter = CabinetAdapter(this, null, rows, columns, carriageEnabled, dir)
    private var step = 0
    private lateinit var currentCabinet: CabinetResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabinet)
        initUi()

        val code = intent?.extras?.getString("code")
        observeOnDb(code)
    }

    private fun observeOnDb(code: String?) {
        if (code != null && code != "") {
            AppDatabase.getInstance().cabinetDao().getLiveData(code).observe(this, Observer {
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
        carriageSwitch.setEnableEffect(false)
        initSteppers()
        initGrid()
        submit.setOnClickListener { addCabinet() }
        extend.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.extend_dialog, null)
            val rowsStepper = (view.findViewById(R.id.rowsCount) as StepperTouch)
            val columnsStepper = (view.findViewById(R.id.columnsCount) as StepperTouch)
            rowsStepper.apply {
                count = rows
                minValue = rows
                maxValue = 10
                sideTapEnabled = true
            }
            columnsStepper.apply {
                count = columns
                minValue = columns
                maxValue = 10
                sideTapEnabled = true
            }
            val dialog = MaterialDialog(this).customView(view = view).cornerRadius(20.0f)
                .positiveButton(text = "تایید") {
                    val newRowsCount = rowsStepper.count
                    val newColumnsCount = columnsStepper.count
                    CoroutineScope(Dispatchers.Main).launch {
                        callWebservice {
                            getServices().extendCabinet(
                                currentCabinet.code,
                                newColumnsCount - columns,
                                newRowsCount - rows
                            )
                        }?.run {
                            AppDatabase.getInstance().cabinetDao().update(this)
                        }
                    }
                }
            dialog.show()

        }
    }

    private fun addCabinet() {
        CoroutineScope(Dispatchers.Main).launch {
            submit.isClickable = false
            callWebservice {
                getServices().cabinet("", rows, columns, 1, if(carriageEnabled)  1 else 0)
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

    private fun updateCarriage(isChecked: Boolean){
        carriageEnabled = isChecked
        adapter.carriageEnabled = carriageEnabled
        adapter.notifyDataSetChanged()
    }

    private fun switchDirection() {
        dir = (dir + 1) % 4
        adapter.dir = dir
        currentCabinet.rotate(dir)
        adapter.notifyDataSetChanged()
        CoroutineScope(Dispatchers.Main).launch {
            AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
        }
    }

    private fun initSteppers() {
        rowsCount.addStepCallback(object : OnStepCallback {
            override fun onStep(value: Int, positive: Boolean) = updateRows(value)
        })

        columnsCount.addStepCallback(object : OnStepCallback {
            override fun onStep(value: Int, positive: Boolean) = updateColumns(value)
        })

        carriageSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateCarriage(isChecked)
        }

        directionSwitch.setOnClickListener {
            switchDirection()
        }

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
                .andAnimate(subHeaderList)
                .alpha(1F)
                .andAnimate(extend)
                .alpha(1F)
                .andAnimate(directionSwitch)
                .alpha(1F)
                .start()
        } else {
            steppers.visibility = View.GONE
            labels.visibility = View.GONE
            submit.visibility = View.GONE
            subHeaderList.alpha = 1F
            extend.alpha = 1F
            directionSwitch.alpha = 1F
        }
        subHeader.text = "برای تغییر وضعیت هر یک از سلول ها روی آن کلیک کنید:"
        header.text = String.format(
            getString(R.string.cabinet_format),
            currentCabinet.code
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

        val cell = currentCabinet.getCell(index, dir)

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
        val cell = currentCabinet.getCell(cellIndex, dir)
        when (option.id % 10) {
            UNUSABLE_OPTION_ID -> changeStatus(cellIndex, false)
            USABLE_OPTION_ID -> changeStatus(cellIndex, true)
            EMPTY_OPTION_ID -> CoroutineScope(Dispatchers.Main).launch {
                callWebservice { getServices().free(currentCabinet.getCell(cellIndex, dir).code.toInt()) }?.run {
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
                callWebservice { getServices().print(cell.code.toInt()) }?.run {
                    toastSuccess("برچسب های قفسه فوق چاپ شد")
                }
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
            val cell = currentCabinet.getCell(index, dir)
            callWebservice {
                getServices().changeStatus(cell.code, isHealthy)
            }?.run {
                cell.isHealthy = isHealthy
                AppDatabase.getInstance().cabinetDao().insert(currentCabinet)
            }
        }
}
