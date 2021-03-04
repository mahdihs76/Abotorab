package ir.nilva.abotorab.view.page.main

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.gc.materialdesign.views.ProgressBarDeterminate
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.helper.*
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import ir.nilva.abotorab.webservices.callWebservice
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.accounting_main.*
import kotlinx.android.synthetic.main.progress_dialog_material.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    companion object {
        val validIps = ArrayList<Pair<String, String>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accounting_main)
        initValues()

        val currentToken: String? = defaultCache()["token"]

        val baseUrl = MyRetrofit.getBaseUrl()
        if (baseUrl == "") {
            connectedServerId.text = "با یکی از دو روش زیر متصل شوید"
        } else {
            val s = baseUrl.split(".")[3]
            connectToNetworkWPA(s.substring(0, s.length - 1))
            if (currentToken.isNullOrEmpty()) {
                showDialog()
            }
            connectedServerId.text = "متصل به سرور : $baseUrl"
        }


        ip.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "شماره امانت‌داری را وارد کنید")
                input(hint = "مثلا: 14", prefill = "10") { _, text ->
                    defaultCache()["depository_code"] = text.toString()
                    connectToNetworkWPA(text.toString())
                    connect2Server("http://192.168.0.$text")
                }
                positiveButton(text = "اتصال")
            }
        }

        automaticIp.setOnClickListener {
            val dialog = MaterialDialog(this).show {
                customView(R.layout.progress_dialog_material)
            }
            CoroutineScope(Dispatchers.Main).launch {
                val customView = dialog.getCustomView()
                val res = connectAutomatic(customView.progress)
                customView.progress.visibility = View.GONE
                if (res) {
                    customView.message.text = "اتصال به سرور با موفقیت انجام شد"
                } else {
                    customView.message.text = "اتصال به سرور با خطا مواجه شد"
                }
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

    private fun initValues() {
        validIps.add(Pair("http://192.168.0.11/", "11"))
        validIps.add(Pair("http://192.168.0.12/", "12"))
        validIps.add(Pair("http://192.168.0.13/", "13"))
        validIps.add(Pair("http://192.168.0.14/", "14"))
        validIps.add(Pair("http://192.168.0.15/", "15"))
        validIps.add(Pair("http://192.168.0.16/", "16"))
    }


    private fun showDialog() {
        val dialog = MaterialDialog(this).show {
            customView(R.layout.progress_dialog_material)
        }
        dialog.show()
        Handler().postDelayed({
            dialog.dismiss()
        }, 5000)
    }

    private fun connect2Server(ip: String) {
        MyRetrofit.setBaseUrl(ip)
    }

    private var numOfServerChecked = 0

    private suspend fun connectAutomatic(progress: ProgressBarDeterminate): Boolean {
        numOfServerChecked = 0
        var result = false
        for (ip in validIps) {
            numOfServerChecked += 1
            notifyDialog(progress)
            connectToNetworkWPA(ip.second)
            delay(5000)
            connect2Server(ip.first)
            defaultCache()["depository_code"] = ip.second
            try {
                getServices().test()
                result = true
                break
            } catch (e: Exception) {
                result = false
            }
        }
        return result
    }

    private fun notifyDialog(
        progress: ProgressBarDeterminate
    ) {
        progress.progress = numOfServerChecked * 100 / validIps.size
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
