package ir.nilva.abotorab

import android.os.Bundle
import android.view.View
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.webservices.WebserviceHelper
import ir.nilva.abotorab.webservices.base.MyRetrofit
import kotlinx.android.synthetic.main.accounting_main.*
import kotlin.concurrent.thread

class AccountingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accounting_main)
        submit.setOnClickListener {
            showLoading()
            thread(true) {
                try {
                    val result = WebserviceHelper.signin(username.text.toString(),
                        password.text.toString())
                    defaultCache()["token"] = result?.token
                    MyRetrofit.newInstance()
                    runOnUiThread {
                        hideLoading()
                        gotoMainPage()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        hideLoading()
                        toastError(e.message.toString())
                    }
                }
            }
        }

        val token : String? = defaultCache()["token"]
        if (token != null) gotoMainPage()
    }

    private fun showLoading() {
        spinKit.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        spinKit.visibility = View.GONE
    }
}
