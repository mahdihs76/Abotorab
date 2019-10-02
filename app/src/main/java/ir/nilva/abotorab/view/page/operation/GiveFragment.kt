package ir.nilva.abotorab.view.page.operation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.showSearchResult
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.fragment_give.*
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
                }?.run {
                    context?.showSearchResult(this) {
                        (activity as GiveActivity).callGiveWS(it)
                    }
                }
                search.visibility = View.VISIBLE
                spinKit.visibility = View.INVISIBLE
            }
        }

    }

}
