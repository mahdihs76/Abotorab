package ir.nilva.abotorab.network

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

fun Context.connectToNetwork(ssid: String, password: String){

    val conf = WifiConfiguration()
    conf.SSID = "\"" + ssid + "\""   // Please note the quotes. String should contain ssid in quotes

    conf.wepKeys[0] = "\"" + password + "\""
    conf.wepTxKeyIndex = 0
    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
    conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)

    conf.preSharedKey = "\""+ password +"\""

    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)

    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    wifiManager.addNetwork(conf)

    val list = wifiManager.configuredNetworks
    for (i in list) {
        if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
            wifiManager.disconnect()
            wifiManager.enableNetwork(i.networkId, true)
            wifiManager.reconnect()
            break
        }
    }
}