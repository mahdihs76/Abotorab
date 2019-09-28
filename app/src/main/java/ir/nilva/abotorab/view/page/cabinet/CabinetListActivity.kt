package ir.nilva.abotorab.view.page.cabinet

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                    fab.hide()
                else if (dy < 0)
                    fab.show()
            }
        })
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        fab.setOnClickListener { gotoCabinetPage() }
        AppDatabase.getInstance().cabinetDao().getAll().observe(this, Observer {
            if (it.isNotEmpty()) {
                adapter.submitList(it)
            }
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

    override fun printCabinet(view: View, code: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            view.background = ContextCompat.getDrawable(
                this@CabinetListActivity,
                R.drawable.disable_bg
            )
            view.isEnabled = false
            try {
                val response = MyRetrofit.getService().printCabinet(code)
                if (response.isSuccessful) {
                    toastSuccess("برچسب های قفسه فوق چاپ شد")
                } else toastError(response.errorBody()?.string() ?: "")
            } catch (e: Exception) {
                toastError(e.message.toString())
            }
            view.background = ContextCompat.getDrawable(
                this@CabinetListActivity,
                R.drawable.print_bg
            )
            view.isEnabled = true
        }
    }

    override fun deleteCabinet(code: Int) {
//        MaterialDialog(this).show {
//            title(text = "قفسه شماره $code حذف شود؟ ")
//            positiveButton(text = "بله") {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = MyRetrofit.getService().deleteCabinet(code)
                        if (response.isSuccessful) {
                            AppDatabase.getInstance().cabinetDao().delete(code)
                        } else {
                            toastError(response.errorBody()?.string() ?: "")
                        }
                    } catch (e: Exception) {
                        toastError(e.message.toString())
                    }

                }
//            }
//            negativeButton(text = "خیر")
//        }

    }
}
