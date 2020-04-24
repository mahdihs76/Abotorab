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
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.view.widget.MarginItemDecoration
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.getServices
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
            callWebservice { getServices().cabinetList() }?.run {
                AppDatabase.getInstance().cabinetDao().clear()
                AppDatabase.getInstance().cabinetDao().insert(this)
            }
        }
    }

    override fun cabinetClicked(code: String) = gotoCabinetPage(code)

    override fun printCabinet(view: View, code: String) {
        CoroutineScope(Dispatchers.Main).launch {
            view.background = ContextCompat.getDrawable(
                this@CabinetListActivity,
                R.drawable.disable_bg
            )
            view.isEnabled = false
            callWebservice { getServices().printCabinet(code) }?.run {
                toastSuccess("برچسب های قفسه فوق چاپ شد")
            }
            view.background = ContextCompat.getDrawable(
                this@CabinetListActivity,
                R.drawable.print_bg
            )
            view.isEnabled = true
        }
    }

    override fun deleteCabinet(code: String, callback: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            callWebservice {
                getServices().deleteCabinet(code)
            }?.run {
                AppDatabase.getInstance().cabinetDao().delete(code)
            }
            callback()
        }
    }
}
