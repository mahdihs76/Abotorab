package ir.nilva.abotorab.view.page.cabinet

import android.os.Bundle
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.gotoCabinetPage
import ir.nilva.abotorab.helper.gotoMainPage
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.model.CabinetResponse
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.view.widget.MarginItemDecoration
import ir.nilva.abotorab.webservices.MyRetrofit
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

    override fun onBackPressed() {
        super.onBackPressed()
        gotoMainPage()
    }
}
