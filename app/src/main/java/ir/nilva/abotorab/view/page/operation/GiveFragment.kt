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
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.model.DeliveryResponse
import ir.nilva.abotorab.webservices.MyRetrofit
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
                try {
                    search.visibility = View.INVISIBLE
                    spinKit.visibility = View.VISIBLE
                    val response = MyRetrofit.getService().deliveryList(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        country.text.toString(),
                        phone.text.toString(),
                        passportId.text.toString()
                    )
                    if (response.isSuccessful) {
                        showSearchResult(response.body())
                    } else toastError(response.toString())
                } catch (e: Exception) {
                    toastError(e.message.toString())
                } finally {
                    search.visibility = View.VISIBLE
                    spinKit.visibility = View.INVISIBLE
                }
            }
        }

    }

    private fun showSearchResult(list: List<DeliveryResponse>?) {
        if (list == null || list.isEmpty()){
            toastError("هیچ موردی یافت نشد")
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
                title.text = item.pilgrim
                subTitle.text = item.enteredAt
                setOnClickListener {
                    dialog.dismiss()
                    callGiveWS(item.hashId)
                }
            })
        }
        dialog.customView(view = view, scrollable = true).show()
    }

    private fun callGiveWS(hashId: String) = CoroutineScope(Dispatchers.Main).launch {
        try {
            MyRetrofit.getService().give(hashId)
            toastSuccess("محموله با موفقیت تحویل داده شد")
        } catch (e: Exception) {
            toastError(e.message.toString())
        }
    }


}
