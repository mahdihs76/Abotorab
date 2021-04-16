package ir.nilva.abotorab.view.page.main

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.accounting_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


val depositories = mapOf(
    Pair("http://192.168.0.11", "11"),
    Pair("http://192.168.0.12", "12"),
    Pair("http://192.168.0.13", "13"),
    Pair("http://192.168.0.14", "14"),
    Pair("http://192.168.0.15", "15"),
    Pair("http://192.168.0.16", "16")
)

class LoginActivity : BaseActivity() {

    private fun updateLabel() {
        if (MyRetrofit.getBaseUrl() == "") {
            connectedServerId.text = "با انتخاب شماره امانت داری به سرور متصل شوید"
        } else {
            val args = getDepositoryNumberFromUrl(MyRetrofit.getBaseUrl())
            if (args == null) {
                connectedServerId.text = "با انتخاب شماره امانت داری به سرور متصل شوید"
            } else {
                connectedServerId.text =
                    String.format("متصل به امانتداری شماره %s", args)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateLabel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accounting_main)

        val currentToken: String? = defaultCache()["token"]

        updateLabel()

        ip.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "شماره امانت‌داری را وارد کنید")
                input(hint = "مثلا: 14", prefill = "14") { _, text ->
                    defaultCache()["depository_code"] = text.toString()
                    val result = connectToNetworkWPA(text.toString())
                    connect2Server("http://192.168.0.$text")
                    if (!result) {
                        toastError("برقراری ارتباط با سرور با خطا مواجه شد")
                    }
                }
                positiveButton(text = "اتصال") {
                    showDialog()
                }
            }
        }

        submit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showLoading()
                callWebservice {
                    getServices().login(
                        username.text.toString(),
                        password.text.toString(),
                        defaultCache()["depository_code"] ?: "10"
                    )
                }?.run {
                    defaultCache()["token"] = token
                    defaultCache()["depository"] = depository
                    MyRetrofit.newInstance()
                    gotoMainPage()
                }
                hideLoading()
            }

        }

        if (currentToken.isNullOrEmpty().not()) gotoMainPage()

    }

    private fun getDepositoryNumberFromUrl(url: String): String? {
        for ((key, value) in depositories) {
            if (key == url) {
                return value
            }
        }
        return null
    }

    private fun showDialog() {
        val dialog = MaterialDialog(this).noAutoDismiss().show {
            customView(R.layout.progress_dialog_material)
        }
        dialog.show()
        Handler().postDelayed({
            updateLabel()
            dialog.dismiss()
        }, 5000)
    }

    private fun connect2Server(ip: String) {
        MyRetrofit.setBaseUrl(ip)
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
