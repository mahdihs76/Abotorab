package ir.nilva.abotorab.view.page.cabinet

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.FormatHelper
import ir.nilva.abotorab.helper.getCell
import ir.nilva.abotorab.helper.getImageResource
import ir.nilva.abotorab.model.CabinetResponse
import kotlinx.android.synthetic.main.cabinet.view.*


class CabinetAdapter(
    val context: Context,
    var cabinet: CabinetResponse?,
    var rows: Int,
    var columns: Int,
    var carriageEnabled: Boolean,
    var dir: Int
) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View =
        LayoutInflater.from(context).inflate(R.layout.cabinet, null).apply {
            if (carriageEnabled && p0 / columns == rows - 1){
                image.setImageResource(R.mipmap.cabinet_empty_long)
            }
            cabinet ?: return@apply
            val cell = cabinet?.getCell(p0, dir)
            cell ?: return@apply
            image.setImageResource(cell.getImageResource())
            codeTextView.visibility = View.VISIBLE
            codeTextView.text = FormatHelper.toPersianNumber(cell.code)
        }

    override fun getItem(p0: Int) = null

    override fun getItemId(p0: Int): Long = System.currentTimeMillis()

    override fun getCount() = rows * columns

}