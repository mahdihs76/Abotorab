package ir.nilva.abotorab.view.page.operation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.toastWarning
import ir.nilva.abotorab.model.DeliveryResponse
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.fragment_give.*
import kotlinx.android.synthetic.main.item_pilgirim.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GiveFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_give, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                search.visibility = View.INVISIBLE
                spinKit.visibility = View.VISIBLE
                callWebservice {
                    getServices().deliveryList(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        country.text.toString(),
                        phone.text.toString(),
                        passportId.text.toString(),
                        true
                    )
                }?.run { showSearchResult(this) }
                search.visibility = View.VISIBLE
                spinKit.visibility = View.INVISIBLE
            }
        }

    }

    private fun showSearchResult(list: List<DeliveryResponse>?) {
        if (list == null || list.isEmpty()) {
            context?.toastWarning("هیچ موردی یافت نشد")
            return
        }
        val view = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
        }
        val dialog = MaterialDialog(context!!)
        list.forEach { item ->
            view.addView(layoutInflater.inflate(R.layout.item_pilgirim, null).apply {
                var cabinetCode = -1
                val packs = item.pack
                if (packs.isNotEmpty()) cabinetCode = packs[0].cell
                title.text = item.pilgrim.country + " از " + item.pilgrim.name
                phoneNumber.text = "شماره تلفن:" + item.pilgrim.phone
                cabinet.text = " شماره قفسه:$cabinetCode"
                subTitle.text = "زمان تحویل:" + item.enteredAt
                setOnClickListener {
                    dialog.dismiss()
                    (activity as GiveActivity).callGiveWS(item.hashId)
                }
            })
        }
        dialog.customView(view = view, scrollable = true).show()
    }

}
