package ir.nilva.abotorab.helper

import com.google.gson.Gson
import ir.nilva.abotorab.ApplicationContext
import ir.nilva.abotorab.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


fun getCountryName(code: String): String {
    val inputStream = ApplicationContext.context.resources.openRawResource(R.raw.nationality)
    val jsonString = readTextFile(inputStream)
    val countries = Gson().fromJson<Array<CountryModel>>(jsonString, Array<CountryModel>::class.java)
    return countries.find { it.alpha3Code == code }?.shortName ?: ""
}


fun readTextFile(inputStream: InputStream): String {
    val outputStream = ByteArrayOutputStream()

    val buf = ByteArray(1024)
    var len = inputStream.read(buf)
    try {
        while (len != -1) {
            outputStream.write(buf, 0, len)
            len = inputStream.read(buf)
        }
        outputStream.close()
        inputStream.close()
    } catch (e: IOException) {

    }

    return outputStream.toString()
}