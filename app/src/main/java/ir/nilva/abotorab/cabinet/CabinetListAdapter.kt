package ir.nilva.abotorab.cabinet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.getColumnsNumber
import ir.nilva.abotorab.helper.getRowsNumber
import ir.nilva.abotorab.webservices.cabinet.CabinetResponse
import kotlinx.android.synthetic.main.cabinet_item.view.*

class CabinetListAdapter(
    private val listener: OnClickCabinetListener
) : ListAdapter<CabinetResponse, CabinetListAdapter.CabinetVH>(CabinetDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CabinetVH(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.cabinet_item,
                    null
                )
        )

    override fun onBindViewHolder(holder: CabinetVH, position: Int) =
        holder.bind(getItem(position), listener)

    class CabinetVH(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            item: CabinetResponse,
            listener: OnClickCabinetListener
        ) = with(view) {
            title.text = String.format(
                resources.getString(R.string.cabinet_format),
                item.code
            )
            subTitle.text = String.format(
                resources.getString(R.string.row_and_column_format),
                item.getRowsNumber(),
                item.getColumnsNumber()
            )
            show.setOnClickListener { listener.cabinetClicked(item) }
            print.setOnClickListener { listener.printCabinet(item.code) }
        }
    }

    object CabinetDiff : DiffUtil.ItemCallback<CabinetResponse>() {
        override fun areItemsTheSame(oldItem: CabinetResponse, newItem: CabinetResponse) =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: CabinetResponse, newItem: CabinetResponse) =
            areItemsTheSame(oldItem, newItem)
    }

    interface OnClickCabinetListener {
        fun cabinetClicked(cabinet: CabinetResponse)
        fun printCabinet(code: Int)
    }

}
