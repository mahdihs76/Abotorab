package ir.nilva.abotorab.view.page.cabinet

import android.os.Bundle
import androidx.lifecycle.Observer
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.helper.gotoCabinetPage
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.helper.toastSuccess
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
        fab.setOnClickListener { gotoCabinetPage() }
        AppDatabase.getInstance().cabinetDao().getAll().observe(this, Observer {
            adapter.submitList(it)
        })
        getCabinetList()
    }

    private fun getCabinetList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = MyRetrofit.getInstance().webserviceUrls.cabinetList()
            if (response.isSuccessful) {
                AppDatabase.getInstance().cabinetDao().clear()
                AppDatabase.getInstance().cabinetDao().insert(response.body()!!)
            }
        }
    }

    override fun cabinetClicked(code: Int) = gotoCabinetPage(code)

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

}
