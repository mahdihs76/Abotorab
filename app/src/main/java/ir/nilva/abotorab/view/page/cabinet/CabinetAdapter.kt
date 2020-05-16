package ir.nilva.abotorab.view.page.cabinet

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.FormatHelper
import ir.nilva.abotorab.helper.getCell
import ir.nilva.abotorab.helper.getImageResource
import ir.nilva.abotorab.helper.getScreenWidth
import ir.nilva.abotorab.model.CabinetResponse
import kotlinx.android.synthetic.main.cabinet.view.*


class CabinetAdapter(
    val activity: Activity,
    var cabinet: CabinetResponse?,
    var rows: Int,
    var columns: Int,
    var carriageEnabled: Boolean,
    var dir: Int
) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View =
        LayoutInflater.from(activity).inflate(R.layout.cabinet, null).apply {
            if (carriageEnabled && p0 / columns == rows - 1) {
                long_image.visibility = View.VISIBLE
                long_image.requestLayout()
                long_image.layoutParams.width = (activity.getScreenWidth() / columns)
                long_image.layoutParams.height = (long_image.layoutParams.width * 1.3).toInt()
                image.visibility = View.GONE
            }
            cabinet ?: return@apply
            val cell = cabinet?.getCell(p0, dir)
            cell ?: return@apply
            if (cell.size > 0) {
                long_image.visibility = View.VISIBLE
                long_image.requestLayout()
                long_image.layoutParams.width = (activity.getScreenWidth() / columns)
                long_image.layoutParams.height = (long_image.layoutParams.width * 1.3).toInt()
                image.visibility = View.GONE
            }
            val needLongCell = carriageEnabled && p0 / columns == rows - 1
            if (needLongCell || cell.size > 0) {
                long_image.setImageResource(cell.getImageResource(true))
            } else {
                image.setImageResource(cell.getImageResource(false))
            }
            codeTextView.visibility = View.VISIBLE
            codeTextView.text = FormatHelper.toPersianNumber(cell.code)
        }

    override fun getItem(p0: Int) = null

    override fun getItemId(p0: Int): Long = System.currentTimeMillis()

    override fun getCount() = rows * columns

}