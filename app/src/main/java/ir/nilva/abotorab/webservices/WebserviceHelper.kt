package ir.nilva.abotorab.webservices

import ir.nilva.abotorab.webservices.base.WebserviceException
import ir.nilva.abotorab.webservices.report.ReportProcess
import ir.nilva.abotorab.webservices.signin.SigninProcess
import ir.nilva.abotorab.webservices.take.TakeProcess
import java.io.IOException

object WebserviceHelper {

    @Throws(IOException::class, WebserviceException::class)
    fun signin(
        username: String,
        password: String
    ) = SigninProcess(username, password).process()

    @Throws(IOException::class, WebserviceException::class)
    fun take(
        firstname: String,
        lastname: String,
        phone: String,
        country: String,
        passportId: String,
        bagCount: Int,
        suitcaseCount: Int,
        pramCount: Int
    ) = TakeProcess(firstname, lastname, phone, country, passportId, bagCount, suitcaseCount, pramCount).process()

    @Throws(IOException::class, WebserviceException::class)
    fun report() = ReportProcess().process()

}
