package ir.nilva.abotorab.view.page.main

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.defaultCache
import ir.nilva.abotorab.helper.get
import ir.nilva.abotorab.helper.gotoMainPage
import ir.nilva.abotorab.helper.set
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.accounting_main.*
import kotlinx.android.synthetic.main.progress_dialog_material.view.*
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
                callWebservice {
                    getServices().login(
                        username.text.toString(),
                        password.text.toString()
                    )
                }?.run {
                    defaultCache()["token"] = token
                    MyRetrofit.newInstance()
                    gotoMainPage()
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
                getServices().test()
                break
            } catch (e: Exception) {
            }
        }
    }

    private fun showLoading() {
        submit.visibility = View.INVISIBLE
        spinKit.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        submit.visibility = View.VISIBLE
        spinKit.visibility = View.GONE
    }
}
