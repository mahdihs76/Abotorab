package ir.nilva.abotorab.view.page.main

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import ir.nilva.abotorab.R
import ir.nilva.abotorab.helper.*
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
                title(text = "شماره امانت‌داری را وارد کنید")
                input(hint = "مثلا: 14", prefill = "10") { _, text ->
                    defaultCache()["depository_code"] = text.toString()
                    if (text.toString() == "10") {
                        connect2Server("http://depository.ceshora.ir/")
                    } else {
                        connectToNetworkWPA("amanatdari$text", "110+salavat")
                        connect2Server("http://192.168.0.$text")
                    }
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
            Log.d("button","clicked")
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
                Log.d("button","clicked4")
            }

        }


        val token: String? = defaultCache()["token"]
        if (token != null) gotoMainPage()

    }

    private fun connect2Server(ip: String) {
        MyRetrofit.setBaseUrl(ip)
    }

    private suspend fun connectAutomatic() {
        val depositories = arrayOf(
            11, 14, 16
        )

        for (text in depositories) {
            try {
                connectToNetworkWPA("amanatdari$text", "110+salavat")
                connect2Server("http://192.168.0.$text")
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
