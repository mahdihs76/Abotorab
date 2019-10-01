package ir.nilva.abotorab.view.page.operation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.db.model.DeliveryEntity
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.view.widget.MarginItemDecoration
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.fragment_recent_gives.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentGivesFragment : Fragment(),
    RecentGivesListener {

    private lateinit var adapter: RecentGivesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_gives, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecentGivesAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(MarginItemDecoration(20))

        AppDatabase.getInstance().deliveryDao()
            .getAll().observe(this@RecentGivesFragment, Observer {
                adapter.submitList(it)
            })
    }

    override fun undoClicked(item: DeliveryEntity) {
        CoroutineScope(Dispatchers.Main).launch {
            callWebservice { getServices().undoDelivery(item.hashId) }?.run {
                context?.toastSuccess("این محموله بازگردانده شد")
                AppDatabase.getInstance().deliveryDao().delete(item)
            }
        }
    }


}

interface RecentGivesListener {
    fun undoClicked(item: DeliveryEntity)
}

