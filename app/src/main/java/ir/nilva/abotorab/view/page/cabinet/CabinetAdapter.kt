package ir.nilva.abotorab.view.page.cabinet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ir.nilva.abotorab.ApplicationContext
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.getCell
import ir.nilva.abotorab.helper.getImageResource
import ir.nilva.abotorab.model.CabinetResponse
import kotlinx.android.synthetic.main.cabinet.view.*

class CabinetAdapter(var cabinet: CabinetResponse?, var rows: Int, var columns: Int): BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View =
        LayoutInflater.from(ApplicationContext.context).inflate(R.layout.cabinet, null).apply {
            cabinet ?: return@apply
            val cell = cabinet?.getCell(p0)
            cell ?: return@apply
            image.setImageResource(cell.getImageResource())
        }

    override fun getItem(p0: Int) = null

    override fun getItemId(p0: Int): Long = System.currentTimeMillis()

    override fun getCount() = rows * columns

}