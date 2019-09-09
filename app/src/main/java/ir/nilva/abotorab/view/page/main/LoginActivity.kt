package ir.nilva.abotorab.view.page.main

import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import kotlinx.android.synthetic.main.accounting_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accounting_main)

        ip.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "آدرس سرور جدید را وارد کنید")
                input(hint = "مثلا: 192.168.100.5", prefill = MyRetrofit.getBaseUrl()){_, text ->
                    connect2Server(text.toString())
                }
                positiveButton(text = "اتصال")
            }
        }
        submit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showLoading()
                try {
                    val response = MyRetrofit.getService().login(
                        username.text.toString(),
                        password.text.toString()
                    )
                    if (response.isSuccessful) {
                        defaultCache()["token"] = response.body()?.token
                        gotoMainPage()
                    } else toastError(response.toString())
                } catch (e: Exception) {
                    toastError(e.message.toString())
                }
                hideLoading()
            }

        }

        val token: String? = defaultCache()["token"]
        if (token != null) gotoMainPage()
    }

    private fun connect2Server(ip: String) {
        MyRetrofit.setBaseUrl(ip)
    }

    private fun showLoading() {
        spinKit.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        spinKit.visibility = View.GONE
    }
}
