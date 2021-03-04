package ir.nilva.abotorab.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import ir.nilva.abotorab.ApplicationContext
import java.util.*


fun connectToNetworkWPA(text: String){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        connectToNetworkWPAOnNewDevice("amanatdari$text", "110+salavat")
    } else {
        connectToNetworkWPA("amanatdari$text", "110+salavat")
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun connectToNetworkWPAOnNewDevice(networkSSID: String, password: String){
    val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
        .setSsid(networkSSID)
        .setWpa2Passphrase(password)
        .setIsHiddenSsid(true)
        .build()
    val networkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .setNetworkSpecifier(wifiNetworkSpecifier)
        .build()

    val connectivityManager : ConnectivityManager = ApplicationContext.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.requestNetwork(networkRequest, NetworkCallback())

}

fun connectToNetworkWPA(networkSSID: String, password: String): Boolean {
    return try {
        val conf = WifiConfiguration()
        conf.SSID =
            "\"" + networkSSID + "\"" // Please note the quotes. String should contain SSID in quotes
        conf.preSharedKey = "\"" + password + "\""
        conf.status = WifiConfiguration.Status.ENABLED
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        conf.hiddenSSID = true; // Put this line to hidden SSID
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
        Log.d("connecting", conf.SSID + " " + conf.preSharedKey)
        val wifiManager =
            ApplicationContext.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.addNetwork(conf)
        Log.d("after connecting", conf.SSID + " " + conf.preSharedKey)
        val list = wifiManager.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + networkSSID + "\"") {
                wifiManager.disconnect()
                wifiManager.enableNetwork(i.networkId, true)
                wifiManager.reconnect()
                Log.d("re connecting", i.SSID + " " + conf.preSharedKey)
                break
            }
        }
        //WiFi Connection success, return true
        true
    } catch (ex: Exception) {
        println(Arrays.toString(ex.stackTrace))
        false
    }
}