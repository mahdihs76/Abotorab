package ir.nilva.abotorab.cabinet

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ir.nilva.abotorab.BaseActivity
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.gotoCabinetPage
import ir.nilva.abotorab.helper.gotoMainPage
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.webservices.base.MyRetrofit
import ir.nilva.abotorab.webservices.cabinet.CabinetResponse
import kotlinx.android.synthetic.main.activity_cabinet_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CabinetListActivity : BaseActivity(), CabinetListAdapter.OnClickCabinetListener {


    private lateinit var adapter: CabinetListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabinet_list)
        adapter = CabinetListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(MarginItemDecoration(20))
        getCabinetList()
        fab.setOnClickListener { gotoCabinetPage() }
    }

    private fun getCabinetList() {
        CoroutineScope(Dispatchers.Main).launch {
            val response = MyRetrofit.getInstance().webserviceUrls.cabinetList()
            if (response.isSuccessful) {
                adapter.submitList(response.body())
            }
        }
    }

    override fun cabinetClicked(cabinet: CabinetResponse) = gotoCabinetPage(cabinet)

    override fun printCabinet(code: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = MyRetrofit.getService().printCabinet(code)
                if (response.isSuccessful) {
                    toastSuccess("برچسب های قفسه فوق چاپ شد")
                } else toastError(response.toString())
            }catch (e: Exception){ toastError(e.message.toString())}
        }
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceHeight
                }
                left = spaceHeight
                right = spaceHeight
                bottom = spaceHeight
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        gotoMainPage()
    }
}
