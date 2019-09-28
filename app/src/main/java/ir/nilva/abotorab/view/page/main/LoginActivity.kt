package ir.nilva.abotorab.view.page.main

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import kotlinx.android.synthetic.main.accounting_main.*
import kotlinx.android.synthetic.main.progress_dialog_material.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ir.nilva.abotorab.R.layout.accounting_main)

        ip.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "آدرس سرور جدید را وارد کنید")
                input(hint = "مثلا: 192.168.100.5", prefill = MyRetrofit.getBaseUrl()) { _, text ->
                    connect2Server(text.toString())
                }
                positiveButton(text = "اتصال")
            }
        }

        automaticIp.setOnClickListener {
            val dialog = MaterialDialog(this).show {
                customView(R.layout.progress_dialog_material)
            }
            CoroutineScope(Dispatchers.Main).launch {
                connectAutomatic()
                val customView = dialog.getCustomView()
                customView.progress.visibility = View.GONE
                customView.message.text = "اتصال به سرور با موفقیت انجام شد"
                Handler().postDelayed({
                    dialog.dismiss()
                }, 1000)
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
                        MyRetrofit.newInstance()
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


    private suspend fun connectAutomatic() {
        val validIps = arrayOf(
            "https://192.168.0.11/",
            "http://depository.ketaabkhaane.ir/",
            "https://192.168.0.12/",
            "https://192.168.0.13/",
            "https://192.168.0.14/",
            "https://192.168.0.15/",
            "https://192.168.0.16/"
        )

        for (ip in validIps) {
            connect2Server(ip)
            try {
                MyRetrofit.getService().test()
                break
            } catch (e: Exception) { }
        }
    }

    private fun showLoading() {
        spinKit.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        spinKit.visibility = View.GONE
    }
}
